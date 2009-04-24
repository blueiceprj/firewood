package com.googlecode.firewood.encoder;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.corpus.Corpus;

public class ProtobufEncoderTest extends AbstractEncoderTest <OutputStream> {

  public void test() throws IOException {
    test(new ProtobufEncoder());
  }

  public void testEncode() throws IOException {
    ProtobufEncoder encoder = new ProtobufEncoder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStream os = encoder.decorate(baos);

    ILoggingEvent[] eventArray = Corpus.makeStandardCorpus();

    for (ILoggingEvent event : eventArray) {
      encoder.encode(event, os);  
    }
    os.flush();

    int nbrBytes = baos.toByteArray().length;
    System.out.println("nbrBytes = " + nbrBytes);
  }

}
