package eu.bosteels.firewood.appenders;

import org.apache.log4j.spi.LoggingEvent;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * SocketAppender that uses an ObjectOutputStream
 */
public class ObjectSocketAppender extends AbstractSocketAppender {

  private ObjectOutputStream objectOutputStream;

  protected void openOutputStream(Socket socket) throws IOException {
    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
  }

  protected void closeOutputStream() throws IOException {
    if (objectOutputStream != null) {
      objectOutputStream.close();
      objectOutputStream = null;
    }
  }

  protected void write(LoggingEvent event) throws IOException {
    objectOutputStream.writeObject(event);
  }
}
