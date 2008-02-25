package com.googlecode.firewood.plugins.intellij.JumpToCode.server;

import com.googlecode.firewood.plugins.intellij.JumpToCode.logic.ServerConfig;
import com.googlecode.firewood.plugins.intellij.JumpToCode.server.JumpToCodeServlet;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.Server;

/**
 */
public class HttpServer {

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
    } catch (Exception e) {
      // TODO
      e.printStackTrace();
    }
  }

  private void start(ServerConfig config) {
    System.out.println("starting HttpServer");
    server = new Server(config.getPortNumber() + 1);
    Context root = new Context(server, "/", Context.NO_SESSIONS);
    root.addServlet(new ServletHolder(new JumpToCodeServlet() ), "/*");
    server.setStopAtShutdown(true);
    try {
      server.start();
    } catch (Exception e) {
      // TODO
      e.printStackTrace();
    }
  }

}
