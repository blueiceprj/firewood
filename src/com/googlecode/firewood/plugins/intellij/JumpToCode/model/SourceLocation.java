package com.googlecode.firewood.plugins.intellij.JumpToCode.model;

import com.intellij.openapi.util.text.StringUtil;

/**
 */
public class SourceLocation {

  private String packageName;

  private String fileName;

  private int lineNumber;

  /** in which project to search (not yet used) */
  private String project;

  /** in which module to search (not yet used) */  
  private String module;

  public SourceLocation() {
  }

  public SourceLocation(String packageName, String fileName, int lineNumber, String project, String module) {
    this.packageName = packageName;
    this.fileName = fileName;
    this.lineNumber = lineNumber;
    this.project = project;
    this.module = module;
  }

  /**
   * construct a SourceLocation from a fully qualified class name (not yet used)
   * @param fullyQualifiedClassName
   */
  public SourceLocation(String fullyQualifiedClassName) {
    this.packageName = StringUtil.getPackageName(fullyQualifiedClassName);
    // determine filename from the className
    String shortName = StringUtil.getShortName(fullyQualifiedClassName);
    // handle inner classes
    // (won't work when class has one or more dollar signs in his name)
    int index = shortName.indexOf("$");
    if (index != -1) {
      this.fileName = shortName.substring(0, index) + ".java";
    } else {
      this.fileName = fileName + ".java";
    }
    this.lineNumber = -1;
    this.project = "";
    this.module = "";    
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public String getPackageName() {
    return packageName;
  }

  @Override
  public String toString() {
    return packageName + "(" + fileName + ":" + lineNumber + "}";
  }

  public String getFileName() {
    return fileName;
  }

  public String getProject() {
    return project;
  }

  public String getModule() {
    return module;
  }

}
