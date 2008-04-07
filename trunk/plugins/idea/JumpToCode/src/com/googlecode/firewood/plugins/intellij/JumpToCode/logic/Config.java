package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

/**
 */
public class Config implements ServerConfig {
  
  public String hostName = "127.0.0.1";
  public int port = 5986;
  public boolean enabled = true;

  //public boolean firstRun = true;

  public String getHostName() {
    //System.out.println("Config.getHostName");
    return hostName;
  }

  public int getPortNumber() {
    //System.out.println("Config.getPortNumber");
    return port;
  }

  public boolean isEnabled() {
    //System.out.println("Config.isEnabled");
    return enabled;
  }

  public String getPort() {
    //System.out.println("Config.getPort");
    return String.valueOf(port);
  }

  public void setHostName(String hostName) {
    //System.out.println("Config.setHostName");
    this.hostName = hostName;
  }

  public void setPort(String port) {
    //System.out.println("Config.setPort");
    try {
      this.port = Integer.parseInt(port);
    } catch (NumberFormatException e) {
      JumpToCodeApplicationComponent.logger.info("user entered invalid port number: " + port);
      // TODO: show error dialog
    }
  }

  public void setEnabled(boolean enabled) {
    //System.out.println("Config.setEnabled");
    this.enabled = enabled;
  }
}
