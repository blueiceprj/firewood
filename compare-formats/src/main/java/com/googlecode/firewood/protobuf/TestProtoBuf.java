package com.googlecode.firewood.protobuf;

import com.googlecode.firewood.protobuf.LoggingProtos.LoggingEvent;
import com.googlecode.firewood.protobuf.LoggingProtos.MapEntry;
import com.googlecode.firewood.protobuf.LoggingProtos.Level;
import com.googlecode.firewood.protobuf.LoggingProtos.LoggerContext;

public class TestProtoBuf {

  public static void main(String[] args) {

    MapEntry mdcEntry = MapEntry.newBuilder()
            .setKey("REMOTE_IP")
            .setValue("10.3.3.5")
            .build();

    System.out.println("mdcEntry = " + mdcEntry);

    LoggingEvent event = LoggingEvent.newBuilder()
        .setLevel(Level.INFO)
        .setMessage("this is my message")
        .setFormattedMessage("this is my formatted message")
        .setLoggerContext(
            LoggerContext.newBuilder()
                .setName("default-context")
                .setBirthTime(4565L))
        .setLoggerName("com.example.foo.Bar")
        .addMdc(mdcEntry)
        .build();

     


    System.out.println("event = " + event);

  }
}
