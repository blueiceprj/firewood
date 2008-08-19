package eu.bosteels.firewood.appenders;

/**
 */
public enum Format {
  Xml (6262), Object(7373);

  private int port;

  Format(int port) {
    this.port = port;
  }

  public int getPort() {
    return port;
  }
}
