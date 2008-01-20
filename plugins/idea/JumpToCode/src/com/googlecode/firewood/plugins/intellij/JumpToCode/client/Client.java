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
    SocketAddress address = new InetSocketAddress("127.0.0.1",5051);

    IoSession session = connector.connect(address).awaitUninterruptibly().getSession();
    session.getConfig().setUseReadOperation(true);

    String xml =
      "<request type=\"JumpToLocation\">\n" +
        " <id>1008</id>" +
        "  <sourceLocation>\n" +
        "    <className>java.lang.String</className>\n" +
        "    <lineNumber>105</lineNumber>\n" +
        "  </sourceLocation>\n" +
        "</request>";

    sendAndReceive(session, xml);
    
//    String request2 =
//      "<request type=\"JumpToLocation\">\n" +
//        " <id>1009</id>" +
//        "  <sourceLocation>\n" +
//        "    <className>org.springframework.jdbc.core.JdbcTemplate</className>\n" +
//        "    <lineNumber>405</lineNumber>\n" +
//        "  </sourceLocation>\n" +
//        "</request>";

    //sendAndReceive(session, request2);
    //

    String request3 =
      "<request type=\"JumpToLocation\">\n" +
        " <id>1009</id>" +
        "  <sourceLocation>\n" +
        "    <className>org.springframework.jdbc.core.JdbcTemplate$SimpleCallableStatementCreator</className>\n" +
        "    <fileName>"+ "JdbcTemplate.java" + "</fileName>\n" +
        "    <lineNumber>1</lineNumber>\n" +
        "  </sourceLocation>\n" +
        "</request>";

    sendAndReceive(session, request3);

    BufferedReader userInput = new BufferedReader(new InputStreamReader( System.in ) );
    while (true) {
      System.out.println("type class:");
      String clazz = userInput.readLine();
      System.out.println("clazz = " + clazz);
      if (clazz.equals("00")) {
        break;
      }
      String request = createRequest(clazz, null);
      session.write(request);
      ReadFuture readFuture = session.read().awaitUninterruptibly();
      if (readFuture.isRead()) {
        System.out.println("resp = \n" + readFuture.getMessage());
      }
      if (readFuture.getException() != null) {
        System.out.println(readFuture.getException().getMessage());
        break;
      }
    }
  }

  private static String sendAndReceive(IoSession session, String xml) {
    session.write(xml);
    try {
      String response = (String) session.read().awaitUninterruptibly().getMessage();
      System.out.println("response = " + response);
      return response;
    } catch (Exception e) {
      session.close(true);
      System.exit(-5);
    }
    return "";
  }

  private static String createRequest(String clazz, String fileName) {
    Request request = new Request();
    SourceLocation sourceLocation = new SourceLocation();
    sourceLocation.setClassName(clazz);
    sourceLocation.setLineNumber(15);
    request.setType(Request.JumpToLocation);
    request.setSourceLocation(sourceLocation);
    if (fileName != null) {
      sourceLocation.setFileName(fileName);
    } 
    return
        "<request type=\"JumpToLocation\">\n" +
          " <id>1008</id>" +
          "  <sourceLocation>\n" +
          "    <className>"+ clazz + "</className>\n" +
          "    <fileName>"+ fileName + "</fileName>\n" +
          "    <lineNumber>105</lineNumber>\n" +
          "  </sourceLocation>\n" +
          "</request>";
  }


  private static class ClientHandler extends IoHandlerAdapter {
  }

}
