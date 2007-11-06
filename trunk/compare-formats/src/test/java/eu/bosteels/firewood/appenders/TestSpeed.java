package eu.bosteels.firewood.appenders;

import junit.framework.TestCase;

/**
 */
public class TestSpeed extends TestCase {

  public void testSpeed() throws InterruptedException {
    SpeedTestClient client = new SpeedTestClient();
    SpeedTestServer.go();
    client.go();
  }
}
