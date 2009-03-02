package com.googlecode.firewood.protobuf;

import ch.qos.logback.classic.spi.*;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.slf4j.Marker;

/**
 */
public class Test {

  public static void main(String[] args) {

    String a1 = "bnbnbnbnm";
    Person person = new Person("Barack", "Obama", 53);
    LoggerContext lc = new LoggerContext();
    ch.qos.logback.classic.Logger logger = lc.getLogger(Test.class);
    ILoggingEvent event = new LoggingEvent("com.example.foo.Bar", logger, Level.INFO, "my log message about {} and {}", null, new Object[] {a1, person});
    System.out.println("event = " + event);

    LoggingProtos.MapEntry mdcEntry = LoggingProtos.MapEntry.newBuilder()
            .setKey("REMOTE_IP")
            .setValue("10.3.3.5")
            .build();


    LoggingProtos.LoggingEvent event2 = LoggingProtos.LoggingEvent.newBuilder()
        .setLevel(LoggingProtos.Level.INFO)
        .setMessage("this is my message")
        .setFormattedMessage("this is my formatted message")
        .setLoggerContext(
            LoggingProtos.LoggerContext.newBuilder()
                .setName("default-context")
                .setBirthTime(4565L))
        .setLoggerName("com.example.foo.Bar")
        .addMdc(mdcEntry)
        .build();



  }

  private LoggingProtos.Level convert(Level level) {
    if (level == Level.DEBUG) {
      return LoggingProtos.Level.DEBUG;
    }
    if (level == Level.INFO) {
      return LoggingProtos.Level.INFO;
    }
    if (level == Level.WARN) {
      return LoggingProtos.Level.WARN;
    }
    if (level == Level.ERROR) {
      return LoggingProtos.Level.ERROR;
    }
    if (level == Level.TRACE) {
      return LoggingProtos.Level.TRACE;
    }
    // this should not happen
    return LoggingProtos.Level.ERROR;  

  }

  private LoggingProtos.LoggerContext convert(LoggerContextVO lc) {
    return LoggingProtos.LoggerContext
        .newBuilder()
        .setBirthTime(lc.getBirthTime())
        .setName(lc.getName())
        .addAllProperties(convert(lc.getPropertyMap()))
        .build();
  }

  private List<LoggingProtos.MapEntry> convert(Map<String,String> map) {
    List<LoggingProtos.MapEntry> list = new ArrayList<LoggingProtos.MapEntry>(map.size());
    for (LoggingProtos.MapEntry entry : list) {
      list.add(LoggingProtos.MapEntry
          .newBuilder()
          .setKey(entry.getKey())
          .setValue(entry.getValue())
          .build());
    }
    return list;
  }

  public LoggingProtos.LoggingEvent convert(ILoggingEvent event) {
    LoggingProtos.LoggingEvent.Builder builder = LoggingProtos.LoggingEvent.newBuilder();
    builder.setLevel(convert(event.getLevel()));
    builder.setMessage(event.getMessage());
    builder.setFormattedMessage(event.getFormattedMessage());
    builder.setLoggerName(event.getLoggerName());
    builder.setThreadName(event.getThreadName());
    builder.setTimestamp(event.getTimeStamp());

    for (Object arg : event.getArgumentArray()) {
      builder.addArguments(arg == null ? "null" : arg.toString());
    }

    for (CallerData callerData : event.getCallerData()) {
      LoggingProtos.CallerData data = LoggingProtos.CallerData.newBuilder()
          .setClassName(callerData.getClassName())
          .setFileName(callerData.getFileName())
          .setLineNumber(callerData.getLineNumber())
          .setMethodName(callerData.getMethodName())
          .build();
      builder.addCallerData(data);  
    }
    builder.setLoggerContext(convert(event.getLoggerContextVO()));
    builder.addAllMdc(convert(event.getMDCPropertyMap()));
    builder.setMarker(convert(event.getMarker()));


    event.getThrowableProxy()

    builder.setThrowable();

    return builder.build();
  }

  private LoggingProtos.Throwable convert(IThrowableProxy proxy) {
    LoggingProtos.Throwable.Builder builder = LoggingProtos.Throwable.newBuilder();
    builder.setMessage();
  }

  private LoggingProtos.Marker convert(org.slf4j.Marker marker) {
    LoggingProtos.Marker.Builder builder = LoggingProtos.Marker.newBuilder();
    builder.setName(marker.getName());
    while (marker.iterator().hasNext()) {
      Marker child = (Marker) marker.iterator().next();
      builder.addMarker(convert(child));
    }
    return builder.build();
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
