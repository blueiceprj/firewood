package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.googlecode.firewood.plugins.intellij.JumpToCode.server.HttpServer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.apache.log4j.Logger;
import org.jdom.Element;

import javax.swing.Icon;
import javax.swing.JComponent;

public class JumpToCodeApplicationComponent implements ApplicationComponent, Configurable,
  JDOMExternalizable {

  static final Logger logger = (Logger) Logger.getInstance(JumpToCodeApplicationComponent.class);

  private Config config = new Config();

  private JumpToCodeConfigurationForm form;

  public JumpToCodeApplicationComponent() {
    logger.debug("constructed JumpToCodeApplicationComponent");
  }

  @Nls
  public String getDisplayName() {
    return "JumpToCode";
  }

  @Nullable
  public Icon getIcon() {
    return IconLoader.findIcon("wi0126-32.png", this.getClass());
  }

  @Nullable
  @NonNls
  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    if (form == null) {
      form = new JumpToCodeConfigurationForm();
    }
    return form.getRootComponent();
  }

  public boolean isModified() {
    return form != null && form.isModified(this.config);
  }

  public void apply() throws ConfigurationException {
    if (form != null) {
      form.getData(config);
      HttpServer.getInstance().configure(config);
    }
  }
  public void reset() {
    if (form != null) {
      form.setData(this.config);
    }
  }
  public void disposeUIResources() {
    form = null;
  }

  public void initComponent() {
     System.out.println("JumpToCodeApplicationComponent.initComponent");
    // if config is different from default, readExternal should have been called
    HttpServer.getInstance().configure(config);

//    if (config.firstRun) {
//      // during the first run (immediately after installation of the plugin)
//      // readExternal will not be called because there is no configuration
//      // => we just start the server with the default config
//      this.createComponent().setVisible(true);
//      HttpServer.getInstance().configure(config);
//    }
    // else we wait until readExternal is called
    //config.firstRun = false;
  }

  public void disposeComponent() {
  }

  @NotNull
  public String getComponentName() {
    return "JumpToCodeApplicationComponent";
  }

  public void readExternal(Element element) throws InvalidDataException {
    System.out.println("JumpToCodeApplicationComponent.readExternal " + element);
    DefaultJDOMExternalizer.readExternal(config, element);
  }

  public void writeExternal(Element element) throws WriteExternalException {
    System.out.println("JumpToCodeApplicationComponent.writeExternal " + element);
    DefaultJDOMExternalizer.writeExternal(config, element);
  }
}