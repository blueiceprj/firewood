package com.googlecode.firewood.plugins.intellij.JumpToCode.model;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 */
public class RequestTypeConverter implements SingleValueConverter {

  public String toString(Object obj) {
    return obj.toString();
  }

  public Object fromString(String name) {
    return RequestType.valueOf(name);
  }

  public boolean canConvert(Class type) {
    return type.equals(RequestType.class);
  }

}

