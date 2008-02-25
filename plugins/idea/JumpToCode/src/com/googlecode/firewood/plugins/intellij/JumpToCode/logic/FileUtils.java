package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.module.Module;
import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
public class FileUtils {

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
    final AtomicBoolean ok = new AtomicBoolean(false);
    for (SourceFile sourceFile : files) {
      final FileEditorManager fem = FileEditorManager.getInstance(sourceFile.project);
      final OpenFileDescriptor ofd = new OpenFileDescriptor(
        sourceFile.project, sourceFile.virtualFile, location.getLineNumber(), 1);
      try {
        SwingUtilities.invokeAndWait(
        new Runnable() {
            public void run() {
              Editor editor = fem.openTextEditor(ofd, true);
              if (editor != null) {
                ok.set(true);
              }
            }
          });
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      if (ok.get()) {
        break;
      }
    }
    return ok.get();
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
      ProjectFileIndex fileIndex = prm.getFileIndex();
      VirtualFile[] dirs = fileIndex
        .getDirectoriesByPackageName(location.getPackageName(), true);
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
