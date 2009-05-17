package com.googlecode.firewood.plugins.intellij.JumpToCode.client;

import junit.framework.TestCase;
import com.intellij.openapi.util.text.StringUtil;
import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;

/**
 */
public class TestClassnames extends TestCase {


  public static final String PACKAGE = "com.googlecode.firewood.plugins.intellij.JumpToCode.client";

  public void testNormalClass() {
    assertNames(TestClassnames.class, PACKAGE, "TestClassnames");
  }

  private static class Inner2 {
  }

  public void testInnerClass() {
    assertNames(Inner2.class, PACKAGE, "TestClassnames$Inner2");
  }

  private void assertNames(Class clazz, String expectedPackageName, String expectedShortName) {
    String fqn = clazz.getName();
    String shortName = StringUtil.getShortName(fqn);
    String simple = clazz.getSimpleName();
    String packageName = StringUtil.getPackageName(fqn);
    System.out.println("fqn = " + fqn);
    System.out.println("shortName = " + shortName);
    System.out.println("simple = " + simple);
    assertEquals(expectedPackageName, packageName);
    assertEquals(expectedShortName, shortName);
    //assertEquals(simple, shortName);
  }

  private static class InnerWithDollarAthTheEnd$ {
  }
  private static class $InnerWithDollarAthTheStart {
  }
  private static class InnerWith$DollarInTheMiddle {
  }


  public void testDolarSigns() {
    assertNames(TestClassnames.InnerWithDollarAthTheEnd$.class, PACKAGE, "TestClassnames$InnerWithDollarAthTheEnd$");
    assertNames(TestClassnames.$InnerWithDollarAthTheStart.class, PACKAGE, "TestClassnames$$InnerWithDollarAthTheStart");
    assertNames(TestClassnames.InnerWith$DollarInTheMiddle.class, PACKAGE, "TestClassnames$InnerWith$DollarInTheMiddle");
  }

}
