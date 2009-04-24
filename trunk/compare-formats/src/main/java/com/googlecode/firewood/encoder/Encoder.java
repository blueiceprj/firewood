package com.googlecode.firewood.encoder;


import java.io.OutputStream;
import java.io.IOException;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface Encoder <T extends OutputStream>{

  void encode(ILoggingEvent event, T output) throws IOException;

  T decorate(OutputStream os) throws IOException;
}
