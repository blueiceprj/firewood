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
  private JCheckBox enabledCheckBox;

  // Method returns the root component of the form
  public JComponent getRootComponent() {
    return rootComponent;
  }

  public void setData(Config data) {
    portField.setText(data.getPort());
    enabledCheckBox.setSelected(data.isEnabled());
    hostnameField.setText(data.getHostName());
  }

  public void getData(Config data) {
    data.setPort(portField.getText());
    data.setEnabled(enabledCheckBox.isSelected());
    data.setHostName(hostnameField.getText());
  }

  @SuppressWarnings({"RedundantIfStatement"})
  public boolean isModified(Config data) {
    if (portField.getText() != null ? !portField.getText().equals(data.getPort()) : data.getPort() != null)
      return true;
    if (enabledCheckBox.isSelected() != data.isEnabled())
      return true;
    if (hostnameField.getText() != null ? !hostnameField.getText().equals(data.getHostName()) : data.getHostName() != null)
      return true;
    return false;
  }
}
