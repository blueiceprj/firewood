package eu.bosteels.firewood.appenders;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 */
public class GenerateLogFile {

  static Logger logger = Logger.getLogger("eu.bosteels.firwood.Test");

  public static void main(String[] args) throws InterruptedException {
    //BasicConfigurator.configure();
    Logger.getRootLogger().info("info message from the root logger");
    Logger.getRootLogger().debug("debug message from the root logger");
    Logger.getRootLogger().warn("warning from the root logger");
    Logger.getRootLogger().info("message with throwable", new RuntimeException("just some exception"));
    logger.info("another info message");
    for (int i=0; i<10000;i++) {
      logger.info("a message from the loop, i=" + i);
      logger.debug("a debug message from the loop, i=" + i);
      Thread.sleep(500);
    }
    test();

  }

  private static void test() {
    logger.info("a message from test");
    logger.info("a message from test with throwable", new IllegalArgumentException("x should not be null"));
  }

}
