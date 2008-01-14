package com.googlecode.firewood.plugins.intellij.JumpToCode.model;

import com.intellij.openapi.util.text.StringUtil;

/**
 */
public class SourceLocation {

  private String className;

  private int lineNumber;

  private String project;

  private String module;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
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
    return getShortClassName() + ".java";
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
