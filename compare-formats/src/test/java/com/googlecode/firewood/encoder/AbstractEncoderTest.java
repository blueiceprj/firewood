package com.googlecode.firewood.encoder;

import junit.framework.TestCase;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.corpus.Corpus;

public class AbstractEncoderTest <T extends OutputStream> extends TestCase {

  public void test(Encoder<T> encoder) throws IOException {
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

}
