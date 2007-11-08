package eu.bosteels.firewood.appenders;

import org.apache.log4j.spi.LoggingEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 */
public class SpeedTestServer {

  private static Map<Format, List<Long>> stats  = new HashMap<Format, List<Long>>();

  public static void main(String[] args) throws Exception {
    go();
  }


  public static void go() {
    new Thread(new Listener(Format.Xml)).start();
    new Thread(new Listener(Format.Object)).start();
  }

  private static class Listener implements Runnable {
    private Format format;

    private Listener(Format format) {
      this.format = format;
      if (stats.get(format) == null) {
        stats.put(format, new ArrayList<Long>());
      }
    }

    public void run() {
      try {
        ServerSocket serverSocket = new ServerSocket(format.getPort());
        System.out.println("listening on port " + format.getPort());
        //noinspection InfiniteLoopStatement
        while (true) {
          Socket socket = serverSocket.accept();
          new SocketHandler(socket, format).run();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static class SocketHandler extends Thread {
    private Socket socket;
    private Format format;

    Receiver createReceiver(Format format, InputStream in) throws Exception {
      switch (format) {
        case Xml    : return new XmlReceiver(in);
        case Object : return new ObjectReceiver(in);
        default: throw new IllegalArgumentException();
      }
    }

    public SocketHandler(Socket socket, Format format) {
      this.socket = socket;
      this.format = format;
    }

    @Override
    public void run() {
      try {
        Receiver receiver = createReceiver(format, socket.getInputStream());
        int count = 0;
        long first = 0;
        while (socket.isConnected()) {
          //Event event = receiver.read();
          receiver.read();
          count++;
          if (count == 1) {
            first = System.currentTimeMillis();
            //System.out.println("message = " + event.message);
          }
          if (count == 5000) {
            long millis = System.currentTimeMillis() - first;
            //System.out.println(format +  " => " + millis + " millis");
            stats.get(format).add(millis);
            if (stats.get(format).size() >= 10) {
              System.out.println("server : " + format + " : " + stats.get(format));
            }
          }
        }
        socket.close();
      } catch (EOFException e) {
        // ignore
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static class Event {
    long timestamp;
    String message;
  }

  private interface Receiver {
    Event read() throws Exception;
  }

  private static class XmlReceiver implements Receiver {
    DataInputStream dais;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;

    XmlReceiver(InputStream in) throws ParserConfigurationException {
      dais = new DataInputStream(in);
      db = dbf.newDocumentBuilder();
    }
    public Event read() throws Exception {
      int len = dais.readInt();
      byte[] bytes = new byte[len];
      dais.readFully(bytes);
      Event event = new Event();
      InputStream in = new ByteArrayInputStream(bytes);
//      Document doc = db.parse(in);
//      Element root = doc.getDocumentElement();
//
//      String timestamp = root.getAttribute("timestamp");
//      event.timestamp = Long.parseLong(timestamp);
//      Element message = (Element) root.getElementsByTagName("log4j:message").item(0);
//      event.message = message.getTextContent();
/*
      System.out.println("timestamp = " + timestamp);
      System.out.println("level = " + root.getAttribute("level"));
      System.out.println("thread = " + root.getAttribute("thread"));
      System.out.println("logger = " + root.getAttribute("logger"));
      Element locationInfo = (Element) root.getElementsByTagName("log4j:locationInfo").item(0);
      System.out.println("class = " + locationInfo.getAttribute("class"));
      System.out.println("method = " + locationInfo.getAttribute("method"));
      System.out.println("file = " + locationInfo.getAttribute("file"));
      System.out.println("line = " + locationInfo.getAttribute("line"));     
*/
      return event;
    }
  }

  private static class ObjectReceiver implements  Receiver {
    ObjectInputStream objectInputStream;
    ObjectReceiver(InputStream in) throws IOException {
      objectInputStream = new ObjectInputStream(in);
    }
    public Event read() throws Exception {
      LoggingEvent logEvent = (LoggingEvent) objectInputStream.readObject();
      Event event = new Event();
      event.timestamp = logEvent.timeStamp;
      event.message = logEvent.getRenderedMessage();
      return event;
    }
  }

}
