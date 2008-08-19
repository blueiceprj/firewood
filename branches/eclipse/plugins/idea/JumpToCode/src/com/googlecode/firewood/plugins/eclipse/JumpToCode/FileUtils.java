package com.googlecode.firewood.plugins.eclipse.JumpToCode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.model.elements.StackFrameContentProvider;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupResult;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.jdi.internal.StackFrameImpl;
import org.eclipse.jdt.launching.sourcelookup.JavaSourceLocator;
import org.eclipse.jdt.internal.compiler.ISourceElementRequestor;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.jdt.internal.launching.JavaSourceLookupUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.UIPlugin;

import com.googlecode.firewood.plugins.intellij.JumpToCode.model.SourceLocation;

public class FileUtils implements com.googlecode.firewood.plugins.FileUtils {

  private Log log = LogFactory.getLog(this.getClass());

  public boolean isReachable(SourceLocation location) {
    log.debug("reachable location = " + location);
    log.error("sourceLocation.fileName = " + location.getFileName());
    log.error("sourceLocation.lineNumber = " + location.getLineNumber());
    log.error("sourceLocation.module = " + location.getModule());
    log.error("sourceLocation.packageName = " + location.getPackageName());
    log.error("sourceLocation.project = " + location.getProject());
    ISourceLookupResult sourceLookupResult = getSourceLookupResult(location);
    if (sourceLookupResult != null) {
      if (sourceLookupResult.getSourceElement() != null) {
        return true;
      }
    }
    return false;
  }

  public boolean jumpToLocation(final SourceLocation location) {
    log.error("sourceLocation.fileName = " + location.getFileName());
    log.error("sourceLocation.lineNumber = " + location.getLineNumber());
    log.error("sourceLocation.module = " + location.getModule());
    log.error("sourceLocation.packageName = " + location.getPackageName());
    log.error("sourceLocation.project = " + location.getProject());
    final IWorkbench workbench = PlatformUI.getWorkbench();
    Display display = workbench.getDisplay();
    display.syncExec(new Runnable() {
      public void run() {
        IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
        ISourceLookupResult sourceLookupResult = getSourceLookupResult(location);
        if (sourceLookupResult != null) {
          if (sourceLookupResult.getSourceElement() != null) {
            IDebugModelPresentation debugModelPresentation = DebugUIPlugin.getModelPresentation();
            IEditorInput editorInput= debugModelPresentation.getEditorInput(sourceLookupResult.getSourceElement());
            log.error("editorInput = " + editorInput);
            String editorId = debugModelPresentation.getEditorId(editorInput, sourceLookupResult.getSourceElement());
            log.error("editorId = " + editorId);
            SourceLookupResult sourceLookupResult2 = new SourceLookupResult(sourceLookupResult.getArtifact(), sourceLookupResult.getSourceElement(), editorId, editorInput);
            DebugUITools.displaySource(sourceLookupResult2, workbenchPage);
//            return true;
          }
        }
      }
    });
    log.error("jumpto location 2 = " + location);
    return true;
  }

  private ISourceLookupResult getSourceLookupResult(SourceLocation sourceLocation) {
    log.error("sourceLocation.fileName = " + sourceLocation.getFileName());
    log.error("sourceLocation.lineNumber = " + sourceLocation.getLineNumber());
    log.error("sourceLocation.module = " + sourceLocation.getModule());
    log.error("sourceLocation.packageName = " + sourceLocation.getPackageName());
    log.error("sourceLocation.project = " + sourceLocation.getProject());
    JavaSourceLookupDirector javaSourceLookupDirector = null;
    try {
      javaSourceLookupDirector = (JavaSourceLookupDirector) DebugPlugin.getDefault().getLaunchManager().newSourceLocator("org.eclipse.jdt.launching.sourceLocator.JavaSourceLookupDirector");
      javaSourceLookupDirector.initializeDefaults(DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations()[0]);
      javaSourceLookupDirector.initializeParticipants();
      /*
      Object[] objects = javaSourceLookupDirector.findSourceElements(sourceLocation.getPackageName() +  "." + sourceLocation.getFileName());
      for (Object object : objects) {
        log.error("obj = " + object);
      }
      ISourceContainer[] sourceContainers = javaSourceLookupDirector.getSourceContainers();
      for (ISourceContainer sourceContainer : sourceContainers) {
        log.error("name conatinaer = " + sourceContainer.getName());
        for (ISourceContainer sourceContainer2 : sourceContainer.getSourceContainers()) {
          log.error("name2 conatinaer = " + sourceContainer2.getName());
          if (sourceContainer2.getName().equals("test2")) {
            for (ISourceContainer sourceContainer3 : sourceContainer2.getSourceContainers()) {
              log.error("name3 conatinaer = " + sourceContainer3.getName());
              if (sourceContainer3.getName().equals("src")) {
                for (ISourceContainer sourceContainer4 : sourceContainer3.getSourceContainers()) {
                  log.error("name4 src conatinaer = " + sourceContainer4.getName());
                }
              }
              if (sourceContainer3.getName().equals("test2")) {
                for (ISourceContainer sourceContainer4 : sourceContainer3.getSourceContainers()) {
                  log.error("name4 conatinaer = " + sourceContainer4.getName());
                }
              }
            }
          }
        }
      }
      */
    } catch (CoreException coreException) {
      log.error("", coreException);
    }
    ISourceLookupResult sourceLookupResult = DebugUITools.lookupSource(sourceLocation.getPackageName() +  "." + sourceLocation.getFileName(), javaSourceLookupDirector);
    if (sourceLookupResult != null) {
      log.error("UUGGHH" + sourceLookupResult);
      log.error("UUGGHH sourceElemn = " + sourceLookupResult.getSourceElement());
      log.error("UUGGHH editorInput = " + sourceLookupResult.getEditorInput());
      log.error("UUGGHH artifact = " + sourceLookupResult.getArtifact());
    }
    return sourceLookupResult;
  }

}
