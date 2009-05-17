package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

/**
 */
public interface ServerConfig {
  
  String getHostName();

  int getPortNumber();

  boolean isEnabled();
}
