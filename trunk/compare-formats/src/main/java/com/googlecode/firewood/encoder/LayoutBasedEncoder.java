package com.googlecode.firewood.encoder;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class LayoutBasedEncoder implements Encoder<DataOutputStream> {

  private Layout<ILoggingEvent> layout;

  public LayoutBasedEncoder(Layout<ILoggingEvent> layout) {
    this.layout = layout;
  }

  public void encode(ILoggingEvent event, DataOutputStream output) throws IOException {
    String str = layout.doLayout(event);
    byte[] bytes = getBytesUTF8(str);
    output.writeInt(bytes.length);
    output.write(bytes);
  }

  private byte[] getBytesUTF8(String str) {
    try {
      return str.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      // Every implementation of the Java platform is required to support UTF-8
      // see http://java.sun.com/j2se/1.5.0/docs/api/java/nio/charset/Charset.html
      return null;
    }
  }

  public DataOutputStream decorate(OutputStream os) {
    return new DataOutputStream(os);
  }
}
