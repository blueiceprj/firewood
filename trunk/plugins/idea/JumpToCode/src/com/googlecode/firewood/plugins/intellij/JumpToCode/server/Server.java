package com.googlecode.firewood.plugins.intellij.JumpToCode.server;

import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory;
import org.apache.log4j.Logger;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.nio.charset.Charset;

import com.googlecode.firewood.plugins.intellij.JumpToCode.logic.ServerConfig;

/**
 */
public class Server {

  protected final Logger logger = (Logger) Logger.getInstance(this.getClass());

  private SocketAcceptor acceptor;

  public boolean isActive() {
    return (acceptor != null && acceptor.isActive());
  }

  public void configure(ServerConfig config) {
    if (isActive()) {
      stop();
    }
    if (config.isEnabled()) {
      start(config);
    }
  }

  private void start(ServerConfig config) {
    acceptor = new NioSocketAcceptor();
    RequestHandler handler = new RequestHandler();
    ProtocolCodecFactory codecFactory = new PrefixedStringCodecFactory(Charset.forName("UTF-8"));
    ProtocolCodecFilter codecFilter = new ProtocolCodecFilter(codecFactory);
    acceptor.getFilterChain().addLast("codec", codecFilter);
    acceptor.setHandler(handler);
    acceptor.setIdleTime(IdleStatus.BOTH_IDLE, 10);
    SocketAddress address = new InetSocketAddress(config.getHostName(), config.getPortNumber());
    try {
      acceptor.bind(address);
    } catch (IOException e) {
      logger.error("failed to bind", e);
      acceptor = null;
    }
  }

  private void stop() {
    acceptor.setCloseOnDeactivation(true);
    acceptor.unbind();
  }

  @Override
  public String toString() {
    return getStatus();
  }

  public String getStatus() {
    if (acceptor == null) {
      return "not initialized";
    }
    return
      "address: "  + acceptor.getLocalAddress()
      + " active: " + acceptor.isActive()
      + " sessions: " + acceptor.getManagedSessionCount();
  }

}
