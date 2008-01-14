package com.googlecode.firewood.plugins.intellij.JumpToCode.model;

/**
 */
public class Response {

  private int id;

  private Integer resultCode;

  private String resultMessage;

  public Response() {
  }

  public Response(Request request) {
    this.id = request.getId();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Integer getResultCode() {
    return resultCode;
  }

  public void setResultCode(Integer resultCode) {
    this.resultCode = resultCode;
  }

  public String getResultMessage() {
    return resultMessage;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
  }

}
