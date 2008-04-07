package com.googlecode.firewood.plugins.intellij.JumpToCode.server;

import com.googlecode.firewood.plugins.intellij.JumpToCode.logic.ServerConfig;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.bio.SocketConnector;
import org.apache.log4j.Logger;

/**
 */
public class HttpServer {

  private final Logger logger = Logger.getLogger(this.getClass());
  
  private static HttpServer instance = new HttpServer();
  private Server server;

  public static HttpServer getInstance() {
    return instance;
  }

  private HttpServer() {
  }

  public boolean isActive() {
    return (server != null) && server.isRunning();
  }

  synchronized public void configure(ServerConfig config) {
    if (isActive()) {
      stop();
    }
    if (config.isEnabled()) {
      start(config);
    }
  }

  private void stop() {
    try {
      server.stop();
      logger.warn("stopped JumpToCode HTTP server");
    } catch (Exception e) {
      logger.error("failed to stop JumpToCode HTTP server", e);
    }
  }

  private void start(ServerConfig config) {
    System.out.println("starting HttpServer");
    server = new Server();
    Connector connector=new SocketConnector();
    connector.setPort(config.getPortNumber());
    connector.setHost(config.getHostName());
    server.setConnectors(new Connector[]{connector});
    Context root = new Context(server, "/", Context.NO_SESSIONS);
    root.addServlet(new ServletHolder(new JumpToCodeServlet() ), "/*");
    server.setStopAtShutdown(true);
    try {
      server.start();
      logger.warn("started JumpToCode HTTP server at "
        + config.getHostName()
        + ":"
        + config.getPortNumber());
    } catch (Exception e) {
      logger.error("failed to start JumpToCode HTTP server", e);
    }
  }

}
