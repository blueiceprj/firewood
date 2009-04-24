package com.googlecode.firewood.protobuf;

import junit.framework.TestCase;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.corpus.Corpus;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.MDC;

import java.io.*;
import java.util.zip.*;

public class ProtoBufConvertorTest extends TestCase {

  private ProtoBufConvertor convertor = new ProtoBufConvertor();

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

  public void testSpeed() throws IOException {
    ILoggingEvent[] eventArray = Corpus.makeStandardCorpus();

//    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream("/tmp/logback-proto." + i + ".zip"));
//    zos.setLevel(1);
//    zos.putNextEntry(new ZipEntry("data"));


    for (int i=0; i<10; i++) {
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/tmp/logback-java.ser." + i));
      OutputStream os = new BufferedOutputStream(new FileOutputStream("/tmp/logback-protobuf.ser" + i));
      GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream("/tmp/logback-proto." + i + ".gz"), 8000);

      DeflaterOutputStream dos = new DeflaterOutputStream(
          new FileOutputStream("/tmp/logback-protobuf.def." + i),
          new Deflater(Deflater.BEST_SPEED));

      serialize(eventArray, oos);
      serializeWithProtobuf("normal", eventArray, os);
      serializeWithProtobuf("gzip  ", eventArray, gos);
      serializeWithProtobuf("def   ", eventArray, dos);
      oos.close();
      os.close();
      gos.close();      
    }
  }

  private void serializeWithProtobuf(String desc, ILoggingEvent[] eventArray, OutputStream os) throws IOException {
    long start = System.currentTimeMillis();
    for (ILoggingEvent event : eventArray) {
      LoggingProtos.LoggingEvent x = convertor.convert(event);
      x.writeTo(os);
    }
    os.flush();
    long millis = System.currentTimeMillis() - start;
    System.out.println("protobuf " + desc + " : " + millis + " millis");
  }

  private void serialize(ILoggingEvent[] eventArray, ObjectOutputStream oos) throws IOException {
    long start = System.currentTimeMillis();
    for (ILoggingEvent event : eventArray) {
      oos.writeObject(event);
    }
    oos.flush();
    long millis = System.currentTimeMillis() - start;
    System.out.println("java serialization: " + millis + " millis");
  }

}
