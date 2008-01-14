package com.googlecode.firewood.plugins.intellij.JumpToCode.logic;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 */
public class JumpToCodeConfigurationForm {
  private JTextField hostnameField;
  private JPanel rootComponent;
  private JTextField portField;
  private JCheckBox enabledOnStartupCheckbox;
  private JCheckBox enabledCheckBox;

  // Method returns the root component of the form
  public JComponent getRootComponent() {
    return rootComponent;
  }

  public void setData(JumpToCodeApplicationComponent data) {
    portField.setText(data.getPort());
    enabledOnStartupCheckbox.setSelected(data.isEnabledOnStartUp());
    enabledCheckBox.setSelected(data.isEnabled());
    hostnameField.setText(data.getHostName());
  }

  public void getData(JumpToCodeApplicationComponent data) {
    data.setPort(portField.getText());
    data.setEnabledOnStartUp(enabledOnStartupCheckbox.isSelected());
    data.setEnabled(enabledCheckBox.isSelected());
    data.setHostName(hostnameField.getText());
  }

  @SuppressWarnings({"RedundantIfStatement"})
  public boolean isModified(JumpToCodeApplicationComponent data) {
    if (portField.getText() != null ? !portField.getText().equals(data.getPort()) : data.getPort() != null)
      return true;
    if (enabledOnStartupCheckbox.isSelected() != data.isEnabledOnStartUp())
      return true;
    if (enabledCheckBox.isSelected() != data.isEnabled())
      return true;
    if (hostnameField.getText() != null ? !hostnameField.getText().equals(data.getHostName()) : data.getHostName() != null)
      return true;
    return false;
  }
}