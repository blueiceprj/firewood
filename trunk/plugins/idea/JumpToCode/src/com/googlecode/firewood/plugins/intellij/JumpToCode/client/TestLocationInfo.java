package com.googlecode.firewood.plugins.intellij.JumpToCode.client;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 */
public class TestLocationInfo extends TestCase {

  public void testLog4j() {
    BasicConfigurator.configure();
    Logger rootLogger = Logger.getRootLogger();
    rootLogger.removeAllAppenders();
    rootLogger.addAppender(new MyAppender());
    Inner.test(rootLogger);
  }

  private static class Inner {
    public static void test(Logger logger) {
      logger.info("my event");
    }
  }

  private static class MyAppender extends AppenderSkeleton {
    protected void append(LoggingEvent event) {
      String fileName = event.getLocationInformation().getFileName();
      System.out.println("fileName = " + fileName);
    }

    public boolean requiresLayout() {
      return false;
    }

    public void close() {
    }
  }

}
