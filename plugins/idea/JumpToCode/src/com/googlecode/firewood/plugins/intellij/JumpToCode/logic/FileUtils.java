package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.module.Module;
import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

/**
 */
public class FileUtils {

  private final static Logger logger = Logger.getLogger(FileUtils.class);

  /**
   * find all matching locations in currently opened projects
   * @param location source location to search for
   * @return all matching locations (can be empty)
   */
  public static boolean isReachable(SourceLocation location) {
    return !findSourceFiles(location).isEmpty();
  }

  /**
   * jump to first matching location
   * @param location the source location to search for
   * @return true if jump was succesful
   */
  public static boolean jumpToLocation(SourceLocation location) {
    List<SourceFile> files = findSourceFiles(location);
    boolean result = false;
    final int lineNumber = location.getLineNumber() - 1;

    for (SourceFile sourceFile : files) {
      final FileEditorManager fem = FileEditorManager.getInstance(sourceFile.project);
      final OpenFileDescriptor ofd = new OpenFileDescriptor(sourceFile.project, sourceFile.virtualFile, lineNumber, 1);

      CodeJumper codeJumper = new CodeJumper(fem, ofd, lineNumber);
      invokeSwing(codeJumper, true);

      if (codeJumper.ok) {
        result = true;
        break;
      }
    }
    return result;
  }

  private static void invokeSwing(Runnable runnable, boolean wait) {
    try {
      if (wait) {
        SwingUtilities.invokeAndWait(runnable);
      } else {
        SwingUtilities.invokeLater(runnable);
      }
    } catch (InterruptedException e) {
      logger.error("Interrupted", e);
    } catch (InvocationTargetException e) {
      logger.error("InvocationTargetException", e);
    }
  }



  private static class CodeJumper implements Runnable {

    private boolean ok = false;
    private FileEditorManager fileEditorManager;
    private OpenFileDescriptor ofd;
    private int lineNumber;

    private CodeJumper(FileEditorManager fileEditorManager, OpenFileDescriptor ofd, int lineNumber ) {
      this.fileEditorManager = fileEditorManager;
      this.ofd = ofd;
      this.lineNumber = lineNumber;
    }

    public void run() {
      Editor editor = fileEditorManager.openTextEditor(ofd, true);
      if (editor != null) {
        TextAttributes attributes = new TextAttributes(Color.BLUE, Color.yellow, Color.blue, EffectType.LINE_UNDERSCORE, Font.PLAIN);
        RangeHighlighter highlighter = editor.getMarkupModel().addLineHighlighter(lineNumber, HighlighterLayer.ERROR, attributes);
        new Thread(new RemoveHighLighter(editor, highlighter)).start();
        ok = true;
      }
    }
  }

  private static void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ignored) {
    }
  }

  private static class RemoveHighLighter implements Runnable {

    private Editor editor;
    private RangeHighlighter highlighter;

    private RemoveHighLighter(Editor editor, RangeHighlighter highlighter) {
      this.editor = editor;
      this.highlighter = highlighter;
    }

    public void run() {
      sleep(3000);
      invokeSwing(new Runnable() {
        public void run() {
          editor.getMarkupModel().removeHighlighter(highlighter);
        }
      }, false);

    }
  }

  private static class SourceFile {
    private Project project;
    Module module;
    VirtualFile virtualFile;

    @Override
    public String toString() {
      String moduleName = (module != null) ? module.getName() : "null";
      String projectName = (project != null) ? project.getName() : "null";
      return String.format("project=[%s] module=[%s] path=[%s}",
          projectName, moduleName, virtualFile.getPath());
    }

    private SourceFile(Project project, Module module, VirtualFile virtualFile) {
      this.project = project;
      this.module = module;
      this.virtualFile = virtualFile;
    }
  }

  private static List<SourceFile> findSourceFiles(SourceLocation location) {
    ProjectManager projectManager = ProjectManager.getInstance();
    Project[] projects = projectManager.getOpenProjects();
    List<SourceFile> matches = new ArrayList<SourceFile>();
    for (Project project : projects) {
      ProjectRootManager prm = ProjectRootManager.getInstance(project);
      PackageIndex packageIndex = PackageIndex.getInstance(project);
      ProjectFileIndex fileIndex = prm.getFileIndex();
      VirtualFile[] dirs = packageIndex.getDirectoriesByPackageName(location.getPackageName(), true);

      for (VirtualFile vf : dirs) {
        VirtualFile child = vf.findChild(location.getFileName());
        if (child != null) {
          SourceFile file = new SourceFile(project, fileIndex.getModuleForFile(child), child);
          matches.add(file);
        }
      }
    }
    return matches;
  }

}
