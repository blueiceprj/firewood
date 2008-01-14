package com.googlecode.firewood.plugins.intellij.JumpToCode.model;

/**
 */
public class Request {

  //private RequestType type;

  private String type;

  public static final String JumpToLocation = "JumpToLocation";
  public static final String CheckLocation  = "CheckLocation";

  private int id;

  private SourceLocation sourceLocation;

  public SourceLocation getSourceLocation() {
    return sourceLocation;
  }

  public void setSourceLocation(SourceLocation sourceLocation) {
    this.sourceLocation = sourceLocation;
  }

  public Request() {
  }

  //  public RequestType getType() {
//    return type;
//  }
//
//  public void setType(RequestType type) {
//    this.type = type;
//  }

  public boolean is(String type) {
    return this.type.equals(type);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

}
