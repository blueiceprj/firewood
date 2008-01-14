package com.googlecode.firewood.plugins.intellij.JumpToCode.client;

import org.apache.mina.common.*;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory;

import java.nio.charset.Charset;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import com.googlecode.firewood.plugins.intellij.JumpToCode.model.Request;
import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;

/**
 */
public class Client {

  public static void main(String[] args) throws IOException {
    IoConnector connector = new NioSocketConnector();
    ProtocolCodecFactory codecFactory = new PrefixedStringCodecFactory(Charset.forName("UTF-8"));
    ProtocolCodecFilter codecFilter = new ProtocolCodecFilter(codecFactory);
    connector.getFilterChain().addLast("codec", codecFilter);
    ClientHandler clientHandler = new ClientHandler();
    connector.setHandler(clientHandler);
    connector.setIdleTime(IdleStatus.BOTH_IDLE, 10);
    SocketAddress address = new InetSocketAddress("127.0.0.1",4749);

    IoSession session = connector.connect(address).awaitUninterruptibly().getSession();

    String xml =
      "<request type=\"JumpToLocation\">\n" +
        " <id>1008</id>" +
        "  <sourceLocation>\n" +
        "    <className>java.lang.String</className>\n" +
        "    <lineNumber>105</lineNumber>\n" +
        "  </sourceLocation>\n" +
        "</request>";

    session.write(xml);
    session.getConfig().setUseReadOperation(true);

    try {
      String response = (String) session.read().awaitUninterruptibly().getMessage();
      System.out.println("response = " + response);
    } catch (Exception e) {
      session.close(true);
      System.exit(-5);
    }

    BufferedReader userInput = new BufferedReader(new InputStreamReader( System.in ) );
    while (true) {
      System.out.println("type class:");
      String clazz = userInput.readLine();
      if (clazz.equals("00")) {
        break;
      }
      String request = createRequest(clazz);
      session.write(request);
      ReadFuture readFuture = session.read().awaitUninterruptibly();
      if (readFuture.isRead()) {
        System.out.println("resp = \n" + readFuture.getMessage());
      }
      if (readFuture.getException() != null) {
        readFuture.getException().printStackTrace();
        break;
      }
    }
  }

  private static String createRequest(String clazz) {
    Request request = new Request();
    SourceLocation sourceLocation = new SourceLocation();
    sourceLocation.setClassName(clazz);
    sourceLocation.setLineNumber(15);
    request.setType(Request.JumpToLocation);
    request.setSourceLocation(sourceLocation);
    return
        "<request type=\"JumpToLocation\">\n" +
          " <id>1008</id>" +
          "  <sourceLocation>\n" +
          "    <className>"+ clazz + "</className>\n" +
          "    <lineNumber>105</lineNumber>\n" +
          "  </sourceLocation>\n" +
          "</request>";
  }


  private static class ClientHandler extends IoHandlerAdapter {
  }

}
