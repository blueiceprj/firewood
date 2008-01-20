package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

/**
 */
public class Config implements ServerConfig {
  
  private String hostName = "0.0.0.0";
  private int port = 5986;
  private boolean enabled = true;

  @SuppressWarnings({"RedundantIfStatement"})
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Config config = (Config) o;

    if (enabled != config.enabled) {
      return false;
    }
    if (port != config.port) {
      return false;
    }
    if (hostName != null ? !hostName.equals(config.hostName) : config.hostName != null) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result;
    result = (hostName != null ? hostName.hashCode() : 0);
    result = 31 * result + port;
    result = 31 * result + (enabled ? 1 : 0);
    return result;
  }

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
    }
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
