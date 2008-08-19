package com.googlecode.firewood.plugins.eclipse.JumpToCode;

import org.eclipse.ui.IStartup;

import com.googlecode.firewood.plugins.intellij.JumpToCode.logic.Config;
import com.googlecode.firewood.plugins.intellij.JumpToCode.server.HttpServer;

/**
 * The activator class controls the plug-in life cycle
 */
public class Startup implements IStartup {

  private Config config = new Config();

  public Startup() {
    System.err.println("UUGGHH 1");
  }

  public void earlyStartup() {
    System.err.println("UUGGHH 4");
    HttpServer.getInstance().configure(config);
    System.err.println("UUGGHH 5");
  }

}
