package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.googlecode.firewood.plugins.intellij.JumpToCode.server.MinaServer;
import com.googlecode.firewood.plugins.intellij.JumpToCode.server.HttpServer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.apache.log4j.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.util.List;
import java.util.ArrayList;

@State(
    name =  "JumpToCodeApplicationComponent",
    storages = {@Storage(id = "JumpToCode", file = "$OPTIONS$/JumpToCode.xml")}
)
public class JumpToCodeApplicationComponent implements ApplicationComponent, Configurable,
 PersistentStateComponent<Config> {

  static final Logger logger = (Logger) Logger.getInstance(JumpToCodeApplicationComponent.class);

  private Config config = new Config();

  private JumpToCodeConfigurationForm form;

  private static List<JumpToCodeApplicationComponent> instances = new ArrayList<JumpToCodeApplicationComponent>();

  public JumpToCodeApplicationComponent() {
    logger.debug("constructed JumpToCodeApplicationComponent");
    instances.add(this);
    logger.debug("instances: " + instances);
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
      //MinaServer.getInstance().configure(config);
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
  }

  public void disposeComponent() {
  }

  @NotNull
  public String getComponentName() {
    return "JumpToCodeApplicationComponent";
  }

  public Config getState() {
    return config;
  }

  public void loadState(Config state) {
    logger.info("loadState");
    if (config.equals(state)) {
      logger.info("loadState but nothing changed");
    } else {
      XmlSerializerUtil.copyBean(state, config);
      logger.debug("loadState: enabled= " + state.isEnabled());
      //MinaServer.getInstance().configure(config);
      HttpServer.getInstance().configure(config);
    }
//    config.hostName = state.hostName;
//    config.port = state.port;
//    config.enabled = state.enabled;
  }
}