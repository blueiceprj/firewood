package com.googlecode.firewood.protobuf;

import junit.framework.TestCase;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.MDC;

public class ProtoBufConvertorTest extends TestCase {

  public void testSimpleEvent() {
    String a1 = "bnbnbnbnm";
    Person person = new Person("Barack", "Obama", 53);
    LoggerContext lc = new LoggerContext();
    Logger logger = lc.getLogger(ProtoBufConvertorTest.class);
    ILoggingEvent event = new LoggingEvent("com.example.foo.Bar", logger, Level.INFO, "my log message about {} and {}", null, new Object[] {a1, person});
    System.out.println("event = " + event);
    ProtoBufConvertor convertor = new ProtoBufConvertor();
    LoggingProtos.LoggingEvent x = convertor.convert(event);
    System.out.println("protobuf =\n" + x);
  }

  public void testEventWithMdc() {
    String a1 = "bnbnbnbnm";
    Person person = new Person("Barack", "Obama", 53);
    LoggerContext lc = new LoggerContext();
    Logger logger = lc.getLogger(ProtoBufConvertorTest.class);

    MDC.getMDCAdapter().put("IP","10.11.12.13");
    MDC.getMDCAdapter().put("xx","abcd");
    

    LoggingEvent event = new LoggingEvent("com.example.foo.Bar", logger, Level.INFO, "my log message about {} and {}", null, new Object[] {a1, person});

    System.out.println("event = " + event);
    ProtoBufConvertor convertor = new ProtoBufConvertor();
    LoggingProtos.LoggingEvent x = convertor.convert(event);
    System.out.println("protobuf =\n" + x);
  }

  private static class Person {
    private String firstName;
    private String lastName;
    private int age;

    private Person(String firstName, String lastName, int age) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
    }

    @Override
    public String toString() {
      return "Person{" +
          "firstName='" + firstName + '\'' +
          ", lastName='" + lastName + '\'' +
          ", age=" + age +
          '}';
    }
  }

}
