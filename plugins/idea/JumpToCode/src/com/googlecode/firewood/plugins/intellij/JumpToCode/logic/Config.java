package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

/**
 */
public class Config implements ServerConfig {
  
  private String hostName = "127.0.0.1";
  private int port = 5986;
  private boolean enabled = true;

  public String getHostName() {
    return hostName;
  }

  public int getPortNumber() {
    return port;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getPort() {
    return String.valueOf(port);
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public void setPort(String port) {
    try {
      this.port = Integer.parseInt(port);
    } catch (NumberFormatException e) {
      JumpToCodeApplicationComponent.logger.info("user entered invalid port number: " + port);
      // TODO: show error dialog
    }
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
