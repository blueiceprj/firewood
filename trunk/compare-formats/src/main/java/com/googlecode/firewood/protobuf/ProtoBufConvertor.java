package com.googlecode.firewood.protobuf;

import ch.qos.logback.classic.spi.*;
import ch.qos.logback.classic.Level;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.slf4j.Marker;

/**
 */
public class ProtoBufConvertor {

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
    LoggingProtos.LoggerContext.Builder builder = LoggingProtos.LoggerContext.newBuilder();
    if (lc.getName() != null) {
      builder.setName(lc.getName());
    }
    return builder
        .setBirthTime(lc.getBirthTime())
        .addAllProperties(convert(lc.getPropertyMap()))
        .build();
  }

  private List<LoggingProtos.MapEntry> convert(Map<String,String> map) {
    List<LoggingProtos.MapEntry> list = new ArrayList<LoggingProtos.MapEntry>(map.size());
    for (Map.Entry<String, String> entry : map.entrySet()) {
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
    if (event.getMDCPropertyMap() != null) {
      builder.addAllMdc(convert(event.getMDCPropertyMap()));
    }
    if (event.getMarker() != null) {
      builder.setMarker(convert(event.getMarker()));
    }
    if (event.getThrowableProxy() != null) {
      builder.setThrowable(convert(event.getThrowableProxy()));
    }
    return builder.build();
  }

  private LoggingProtos.Throwable convert(IThrowableProxy proxy) {
    LoggingProtos.Throwable.Builder builder = LoggingProtos.Throwable.newBuilder();
    builder
        .setMessage(proxy.getMessage())
        .setClassName(proxy.getClassName())
        .setCommonFrames(proxy.getCommonFrames());
    if (proxy.getCause() != null) {
      builder.setCause(convert(proxy.getCause()));
    }
    for (StackTraceElementProxy steProxy : proxy.getStackTraceElementProxyArray()) {
      builder.addStackTraceElements(convert(steProxy.getStackTraceElement()));
    }
    return builder.build();
  }

  private LoggingProtos.StackTraceElement convert(StackTraceElement ste) {
    return LoggingProtos.StackTraceElement
        .newBuilder()
        .setDeclaringClass(ste.getClassName())
        .setFileName(ste.getFileName())
        .setLineNumber(ste.getLineNumber())
        .setMethodName(ste.getMethodName())
        .build();
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

}
