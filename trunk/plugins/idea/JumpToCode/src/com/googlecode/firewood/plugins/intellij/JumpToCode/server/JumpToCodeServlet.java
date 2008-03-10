package com.googlecode.firewood.plugins.intellij.JumpToCode.server;

import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;
import com.googlecode.firewood.plugins.intellij.JumpToCode.logic.FileUtils;
import com.googlecode.firewood.plugins.intellij.JumpToCode.logic.FileCopyUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 */
public class JumpToCodeServlet extends HttpServlet {

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");
    String operation = getParameter(request, "operation", "o", "jump");
    if (operation.equals("form")) {
      form(response);
      return;
    }
    if (operation.equals("jump")) {
      jump(request, response, false);
      return;
    }
    if (operation.equals("test")) {
      jump(request, response, true);
      return;
    }
    error(response, "Unexpected operation");
  }

  private static String getParameter(HttpServletRequest request, String shortName, String longName) {
    String value = request.getParameter(longName);
    if (value == null) {
      value = request.getParameter(shortName);
    }
    return value;
  }

  private static String getParameter(HttpServletRequest request, String shortName, String longName, String defaultValue) {
    String value = getParameter(request, shortName, longName);
    return (value != null) ? value : defaultValue;
  }

  private void jump(HttpServletRequest request, HttpServletResponse response, boolean test) throws IOException {
    String packageName = getParameter(request, "p", "packageName");
    String fileName = getParameter(request, "f", "fileName");
    String className = getParameter(request, "c", "className");
    int lineNumber = parseInt(getParameter(request, "l", "lineNumber"), 0);
    String project = request.getParameter("project");
    String module = request.getParameter("module");

    SourceLocation location;
    if (packageName != null && fileName != null) {
      location = new SourceLocation(packageName, fileName, lineNumber, project, module);
    } else {
      if (className != null) {
        location = new SourceLocation(className);
      } else {
        error(response, "either (packageName,fileName) or (className) is required");
        return;
      }
    }
    boolean ok;
    if (test) {
      ok = FileUtils.isReachable(location);
    } else {
      ok = FileUtils.jumpToLocation(location);
    }
    if (ok) {
      response.setStatus(HttpServletResponse.SC_OK);
      if (test) {
        response.getWriter().println("OK, found " + location);
      } else {
        response.getWriter().println("OK, jumped to " + location);
      }
    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().println("Not found: " + location);
    }
  }

  private void form(HttpServletResponse response) throws IOException {
    FileCopyUtils.copy(
      getClass().getResourceAsStream("form.html"),
      response.getOutputStream());
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private int parseInt(String value, int defaultValue) {
    if (value == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private void error(HttpServletResponse response, String message) throws IOException {
    response.getWriter().println(message);
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

}
