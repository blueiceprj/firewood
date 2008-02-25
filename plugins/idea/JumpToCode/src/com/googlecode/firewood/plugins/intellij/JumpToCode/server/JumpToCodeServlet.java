package com.googlecode.firewood.plugins.intellij.JumpToCode.server;

import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;
import com.googlecode.firewood.plugins.intellij.JumpToCode.logic.FileUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.*;

/**
 */
public class JumpToCodeServlet extends HttpServlet {

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");
    String command = request.getParameter("command");

    if (command == null) {
      error(response, "command is required");
      return;
    }

    if ("form".equals(command)) {
      form(response);
      return;
    }

    if ("jump".equals(command)) {
      jump(request, response, false);
      return;
    }

    if ("test".equals(command)) {
      jump(request, response, true);
      return;
    }

    error(response, "Unexpected command");

  }

  private void jump(HttpServletRequest request, HttpServletResponse response, boolean test) throws IOException {
    String className = request.getParameter("className");
    String fileName = request.getParameter("fileName");
    int lineNumber = parseInt(request.getParameter("lineNumber"), 0);
    String project = request.getParameter("project");
    String module = request.getParameter("module");

    if (className == null) {
      error(response, "className is required");
      return;
    }
    if (fileName == null) {
      error(response, "fileName is required");
      return;
    }
    SourceLocation location = new SourceLocation(className, fileName, lineNumber, project, module);

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
    PrintWriter writer = response.getWriter();
    // TODO : read from file (classpath)
    String html =
      "<html>\n" +
        "<body>\n" +
        "<form action='/' method='GET' >\n" +
        "\n" +
        "  <table border='0'>\n" +
        "    <tr>\n" +
        "      <td><label for='command'>command:</label></td>\n" +
        "      <td>\n" +
        "        <input id='command'      name='command' type='radio' value='jump'/>jump\n" +
        "        <input checked='checked' name='command' type='radio' value='test' />test\n" +
        "      </td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <td><label for='className'>className:</label></td>\n" +
        "      <td><input type='text' name='className' id='className' size='80' value='java.lang.String'/></td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <td><label for='fileName'>fileName</label></td>\n" +
        "      <td><input type='text' name='fileName' id='fileName' size='80' value='String.java'/></td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <td><label for='lineNumber'>lineNumber</label></td>\n" +
        "      <td><input type='text' name='lineNumber' id='lineNumber' value='120'/></td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <td>&nbsp;</td>\n" +
        "      <td><input type='submit'/></td>\n" +
        "    </tr>\n" +
        "  </table>\n" +
        "</form>\n" +
        "</body>\n" +
        "</html>";
    writer.println(html);
    writer.flush();
    response.setStatus(HttpServletResponse.SC_OK);

//    Reader reader = new InputStreamReader(getClass().getResourceAsStream("form.html"));
//    LineNumberReader lineNumberReader = new LineNumberReader(reader);
//    lineNumberReader.readLine();
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
