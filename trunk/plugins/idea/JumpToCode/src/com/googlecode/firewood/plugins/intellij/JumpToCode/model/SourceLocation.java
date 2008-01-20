package com.googlecode.firewood.plugins.intellij.JumpToCode.model;

import com.intellij.openapi.util.text.StringUtil;

/**
 */
public class SourceLocation {

  private String className;

  private String fileName;

  private int lineNumber;

  private String project;

  private String module;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public String getPackageName() {
    return StringUtil.getPackageName(className);
  }

  @Override
  public String toString() {
    return className + ":" + lineNumber;
  }

  public String getShortClassName() {
    return StringUtil.getShortName(className);
  }

  public String getFileName() {
    if (fileName != null) {
      return fileName;
    }
    // determine filename from the className
    String shortName = getShortClassName();
    // handle inner classes
    // (won't work when class has one or more dollar signs in his name)  
    int index = shortName.indexOf("$");
    String fileName = shortName;
    if (index != -1) {
      fileName = shortName.substring(0, index);
    }
    return fileName + ".java";
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }
}
