package eu.bosteels.firewood.appenders;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 */
public class SpeedTestClient {

  private Logger logger = Logger.getLogger("eu.bosteels.logviewer.test");

  private Map<Format, List<Long>> stats  = new HashMap<Format, List<Long>>();

  public static void main(String[] args) throws InterruptedException {
    SpeedTestClient test = new SpeedTestClient();
    test.go();
  }

  public void go() throws InterruptedException {
    for (int i=0;i<11; i++) {
      run(Format.Xml);
      run(Format.Object);
    }
    System.out.println("Client: Xml = " + stats.get(Format.Xml));
    System.out.println("Client: Object = " + stats.get(Format.Object));
    LogManager.shutdown();
  }

  private AbstractSocketAppender createAppender(Format format) {
    switch (format) {
      case Xml    : return new XmlSocketAppender();
      case Object : return new ObjectSocketAppender();
      default: throw new IllegalArgumentException();
    }
  }

  private void run(Format format) throws InterruptedException {
    if (stats.get(format) == null) {
      stats.put(format, new ArrayList<Long>());
    }
    AbstractSocketAppender appender = createAppender(format);
    appender.setRemoteHost("localhost");
    appender.setPort(format.getPort());
    appender.setLocationInfo(false);
    appender.activateOptions();
    appender.setThreshold(Level.ALL);
    Logger.getRootLogger().removeAllAppenders();
    Logger.getRootLogger().addAppender(appender);
    long start = System.currentTimeMillis();
    for (int i=0; i<2500; i++) {
      logger.warn("just some log message of average length, certainly not extremely long, nr=" + i);
    }
    MDC.put("myKey", "myValue");
    for (int i=0; i<2500; i++) {
      logger.info("some cool message: " + i);
    }
    long millis = System.currentTimeMillis() - start;
    stats.get(format).add(millis);
    Logger.getRootLogger().removeAllAppenders();
  }

}
