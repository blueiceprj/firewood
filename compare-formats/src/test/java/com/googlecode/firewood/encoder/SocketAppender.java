package com.googlecode.firewood.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.corpus.Corpus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;

public class SocketAppender <T extends OutputStream> {
 
  private Encoder<T> encoder;

  public void setEncoder(Encoder<T> encoder) {
    this.encoder = encoder;
  }

  public void start() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    T os = encoder.decorate(baos);

    ILoggingEvent[] eventArray = Corpus.makeStandardCorpus();

    for (ILoggingEvent event : eventArray) {
      encoder.encode(event, os);
    }
    os.flush();

    int nbrBytes = baos.toByteArray().length;
    System.out.println("nbrBytes = " + nbrBytes);

  }

  public static void main(String[] args) throws IOException {
    SocketAppender<OutputStream> appender = new SocketAppender<OutputStream>();
    appender.setEncoder(new ProtobufEncoder());
    appender.start();

    SocketAppender<ObjectOutputStream> app = new SocketAppender<ObjectOutputStream>();
    app.setEncoder(new ObjectEncoder());
    app.start();

  }

}
