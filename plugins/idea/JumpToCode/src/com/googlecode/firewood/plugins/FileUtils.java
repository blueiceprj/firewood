package com.googlecode.firewood.plugins;

import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;

/**
 */
public interface FileUtils {

  /**
   * find all matching locations in currently opened projects
   * 
   * @param location
   *                source location to search for
   * @return all matching locations (can be empty)
   */
  public boolean isReachable(SourceLocation location);

  /**
   * jump to first matching location
   * 
   * @param location
   *                the source location to search for
   * @return true if jump was succesful
   */
  public boolean jumpToLocation(SourceLocation location);

}
