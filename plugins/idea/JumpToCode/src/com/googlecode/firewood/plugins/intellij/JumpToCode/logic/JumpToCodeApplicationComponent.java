package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.googlecode.firewood.plugins.intellij.JumpToCode.server.Server;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 */

@State(
    name = JumpToCodeApplicationComponent.COMPONENT_NAME,
    storages = {@Storage(id = "JumpToCode", file = "$OPTIONS$/JumpToCode.xml")}
)
public class JumpToCodeApplicationComponent
  implements ApplicationComponent, Configurable, ServerConfig,
  PersistentStateComponent<JumpToCodeApplicationComponent> {

  public static final String COMPONENT_NAME = "JumpToCodeApplicationComponent";

  final Logger logger = Logger.getInstance("JumpToCodeApplicationComponent");

  private Server server;

  private boolean enabledOnStartUp = true;

  private String hostName = "0.0.0.0";

  private int port = 4748;

  private JumpToCodeConfigurationForm form;

  private boolean enabled;

  public JumpToCodeApplicationComponent() {
    server = new Server();
    if (enabledOnStartUp) {
      server.configure(this);
    }
  }

  public boolean isEnabledOnStartUp() {
    return enabledOnStartUp;
  }

  public void setEnabledOnStartUp(boolean enabledOnStartUp) {
    this.enabledOnStartUp = enabledOnStartUp;
  }

  public String getHostName() {
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public int getPortNumber() {
    return port;
  }

  public String getPort() {
    return String.valueOf(port);
  }

  public void setPort(String port) {
    try {
      this.port = Integer.parseInt(port);
    } catch (NumberFormatException e) {
      logger.info("user entered invalid port number: " + port);
    }
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
    return form != null && form.isModified(this);
  }

  public void apply() throws ConfigurationException {
    if (form != null) {
      // Get data from form to component
      form.getData(this);
      server.configure(this);
    }
  }
  public void reset() {
    if (form != null) {
      // Reset form data from component
      form.setData(this);
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

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public JumpToCodeApplicationComponent getState() {
    return this;
  }

  public void loadState(JumpToCodeApplicationComponent state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}