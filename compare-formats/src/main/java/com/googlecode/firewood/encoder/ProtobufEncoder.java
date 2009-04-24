package com.googlecode.firewood.encoder;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.io.OutputStream;
import java.io.IOException;

import com.googlecode.firewood.protobuf.ProtoBufConvertor;
import com.googlecode.firewood.protobuf.LoggingProtos;

/**
 * Encoder that uses Protobuf as wire-format
 */
public class ProtobufEncoder implements Encoder<OutputStream>{

  private ProtoBufConvertor convertor = new ProtoBufConvertor();

  public void encode(ILoggingEvent event, OutputStream output) throws IOException {
    LoggingProtos.LoggingEvent ev = convertor.convert(event);
    ev.writeTo(output);
  }

  public OutputStream decorate(OutputStream os) throws IOException {
    return os;
  }
}
