package eu.bosteels.firewood.appenders;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.AppenderSkeleton;

/**
 * Abstract base-class for Socket Appenders
 * <p>
 * Heavily based on org.apache.log4j.net.SocketAppender
 * </p>
 */
public abstract class AbstractSocketAppender extends AppenderSkeleton {
  /**
   The default port number of remote logging server (4560).
   @since 1.2.15
   */
  static public final int DEFAULT_PORT                 = 4560;

  /**
   The default reconnection delay (30000 milliseconds or 30 seconds).
   */
  static final int DEFAULT_RECONNECTION_DELAY   = 30000;

  /**
   We remember host name as String in addition to the resolved
   InetAddress so that it can be returned via getOption().
   */
  String remoteHost;

  InetAddress address;
  int port = DEFAULT_PORT;

  int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
  boolean locationInfo = false;
  private String application;

  private Connector connector;

  public AbstractSocketAppender() {
  }

  /**
   Connects to remote server at <code>address</code> and <code>port</code>.
   * @param address address of remote server
   * @param port port of remote server
   */
  public AbstractSocketAppender(InetAddress address, int port) {
    this.address = address;
    this.remoteHost = address.getHostName();
    this.port = port;
    connect(address, port);
  }

  /**
   Connects to remote server at <code>host</code> and <code>port</code>.
   * @param host hostname of remote server
   * @param port port of remote server
   */
  public AbstractSocketAppender(String host, int port) {
    this.port = port;
    this.address = getAddressByName(host);
    this.remoteHost = host;
    connect(address, port);
  }

  /**
   Connect to the specified <b>RemoteHost</b> and <b>Port</b>.
   */
  public void activateOptions() {
    connect(address, port);
  }

  /**
   * Close this appender.
   *
   * <p>This will mark the appender as closed and call then {@link
   * #cleanUp} method.
   * */
  synchronized public void close() {
    if(closed)
      return;
    this.closed = true;
    cleanUp();
  }

  /**
   * Drop the connection to the remote host and release the underlying
   * connector thread if it has been created
   * */
  public void cleanUp() {
    try {
      closeOutputStream();
    } catch(IOException e) {
      LogLog.error("Could not close outputStream.", e);
    }
    if(connector != null) {
      //LogLog.debug("Interrupting the connector.");
      connector.interrupted = true;
      connector = null;  // allow gc
    }
  }

  abstract protected void closeOutputStream() throws IOException;

  abstract protected void openOutputStream(Socket socket) throws IOException;

  abstract protected void write(LoggingEvent event) throws IOException;

  void connect(InetAddress address, int port) {
    if (this.address == null) {
      return;
    }
    try {
      // First, close the previous connection if any.
      cleanUp();
      openOutputStream(new Socket(address, port));

    } catch(IOException e) {

      String msg = "Could not connect to remote log4j server at ["
        +address.getHostName()+"].";
      if(reconnectionDelay > 0) {
        msg += " We will try again later.";
        fireConnector(); // fire the connector thread
      } else {
        msg += " We are not retrying.";
        errorHandler.error(msg, e, ErrorCode.GENERIC_FAILURE);
      }
      LogLog.error(msg);
    }
  }


  public void append(LoggingEvent event) {
    if(event == null)
      return;

    if(address==null) {
      errorHandler.error("No remote host is set for SocketAppender named \""+
        this.name+"\".");
      return;
    }

    try {

      if(locationInfo) {
        event.getLocationInformation();
      }
      if (application != null) {
        event.setProperty("application", application);
      }
      write(event);
      //LogLog.debug("=========Flushing.");

    } catch(IOException e) {
      cleanUp();
      LogLog.warn("Detected problem with connection: "+e);
      if(reconnectionDelay > 0) {
        fireConnector();
      } else {
        errorHandler.error("Detected problem with connection, not reconnecting.", e,
          ErrorCode.GENERIC_FAILURE);
      }
    }
  }

  void fireConnector() {
    if(connector == null) {
      LogLog.debug("Starting a new connector thread.");
      connector = new Connector();
      connector.setDaemon(true);
      connector.setPriority(Thread.MIN_PRIORITY);
      connector.start();
    }
  }

  static
  InetAddress getAddressByName(String host) {
    try {
      return InetAddress.getByName(host);
    } catch(Exception e) {
      LogLog.error("Could not find address of ["+host+"].", e);
      return null;
    }
  }

  /**
   * The SocketAppender does not use a layout. Hence, this method
   * returns <code>false</code>.
   * */
  public boolean requiresLayout() {
    return false;
  }

  /**
   * The <b>RemoteHost</b> option takes a string value which should be
   * the host name of the server where a {@link org.apache.log4j.net.SocketNode} is
   * running.
   * @param host host name of the server
   **/
  public void setRemoteHost(String host) {
    address = getAddressByName(host);
    remoteHost = host;
  }

  /**
   * @return value of the <b>RemoteHost</b> option.
   */
  public String getRemoteHost() {
    return remoteHost;
  }

  /**
   * @param port positive integer representing the port where the
   * server is waiting for connections.
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * @return value of the <b>Port</b> option.
   */
  public int getPort() {
    return port;
  }

  /**
   By default no location information is sent to the server.
   * @param locationInfo If true, the information sent to the remote host will include location
   */
  public void setLocationInfo(boolean locationInfo) {
    this.locationInfo = locationInfo;
  }

  /**
   * @return value of the <b>LocationInfo</b> option.
   */
  public boolean getLocationInfo() {
    return locationInfo;
  }

  public void setApplication(String lapp) {
    this.application = lapp;
  }

  public String getApplication() {
    return application;
  }

  public void setReconnectionDelay(int delay) {
    this.reconnectionDelay = delay;
  }

  public int getReconnectionDelay() {
    return reconnectionDelay;
  }

  /**
   The Connector will reconnect when the server becomes available
   again.  It does this by attempting to open a new connection every
   <code>reconnectionDelay</code> milliseconds.

   <p>It stops trying whenever a connection is established. It will
   restart to try reconnect to the server when previpously open
   connection is droppped.
   */
  class Connector extends Thread {

    boolean interrupted = false;

    public
    void run() {
      Socket socket;
      while(!interrupted) {
        try {
          sleep(reconnectionDelay);
          LogLog.debug("Attempting connection to "+address.getHostName());
          socket = new Socket(address, port);
          synchronized(this) {
            openOutputStream(socket);
            connector = null;
            LogLog.debug("Connection established. Exiting connector thread.");
            break;
          }
        } catch(InterruptedException e) {
          LogLog.debug("Connector interrupted. Leaving loop.");
          return;
        } catch(java.net.ConnectException e) {
          LogLog.debug("Remote host "+address.getHostName()
            +" refused connection.");
        } catch(IOException e) {
          LogLog.debug("Could not connect to " + address.getHostName()+
            ". Exception is " + e);
        }
      }
    }

  }

}
