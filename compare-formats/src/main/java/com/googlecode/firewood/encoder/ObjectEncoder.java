package com.googlecode.firewood.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Encoder that uses plain Java Serialization
 */
public class ObjectEncoder implements Encoder<ObjectOutputStream> {

  public void encode(ILoggingEvent event, ObjectOutputStream output) throws IOException {
    output.writeObject(event);
  }

  public ObjectOutputStream decorate(OutputStream os) throws IOException {
    return new ObjectOutputStream(os);
  }
}
