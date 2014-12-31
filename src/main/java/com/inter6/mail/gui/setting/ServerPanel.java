package com.inter6.mail.gui.setting;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.model.AuthOption;
import com.inter6.mail.module.AppConfig;

@Component
public class ServerPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = -7540651743867028995L;

	@Autowired
	private AppConfig appConfig;

	private final JTextField hostFiled = new JTextField(40);
	private final JTextField portField = new JTextField(5);
	private final JCheckBox sslCheckBox = new JCheckBox("SSL");
	private final JTextField idField = new JTextField(20);
	private final JPasswordField passwordField = new JPasswordField(20);
	private final JComboBox authOptionBox = new JComboBox(AuthOption.allItems());

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel hostPanel = new JPanel(new FlowLayout());
		{
			hostPanel.add(new JLabel("Host"));
			hostPanel.add(this.hostFiled);
			hostPanel.add(new JLabel("Port"));
			hostPanel.add(this.portField);
			hostPanel.add(this.sslCheckBox);
		}
		this.add(hostPanel, BorderLayout.NORTH);

		JPanel accountPanel = new JPanel(new FlowLayout());
		{
			accountPanel.add(new JLabel("ID"));
			accountPanel.add(this.idField);
			accountPanel.add(new JLabel("PW"));
			accountPanel.add(this.passwordField);

			this.authOptionBox.addActionListener(this.authChangeEvent);
			this.authOptionBox.setSelectedIndex(0);
			accountPanel.add(this.authOptionBox);
		}
		this.add(accountPanel, BorderLayout.CENTER);
	}

	public Map<String, Object> getData() throws Throwable {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("server.host", this.hostFiled.getText());
		data.put("server.port", this.portField.getText());
		data.put("server.ssl", this.sslCheckBox.isSelected());
		data.put("user.id", this.idField.getText());
		data.put("user.password", this.passwordField.getText());
		data.put("server.authOption", this.authOptionBox.getSelectedItem());
		return data;
	}

	private final ActionListener authChangeEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			AuthOption authOption = (AuthOption) ServerPanel.this.authOptionBox.getSelectedItem();
			if (authOption == AuthOption.NONE) {
				ServerPanel.this.setEnableAccountField(false);
			} else {
				ServerPanel.this.setEnableAccountField(true);
			}
		}
	};

	private void setEnableAccountField(boolean isEnable) {
		this.idField.setEnabled(isEnable);
		this.idField.setEditable(isEnable);
		this.passwordField.setEnabled(isEnable);
		this.passwordField.setEditable(isEnable);
	}

	@Override
	public void loadConfig() {
		this.hostFiled.setText(this.appConfig.getString("server.host"));
		this.portField.setText(this.appConfig.getString("server.port"));
		this.sslCheckBox.setSelected(this.appConfig.getBoolean("server.ssl", false));
		this.idField.setText(this.appConfig.getString("user.id"));
		this.passwordField.setText(this.appConfig.getString("user.password"));
		this.authOptionBox.setSelectedIndex(AuthOption.parse(this.appConfig.getString("server.authOption")).ordinal());
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("server.host", this.hostFiled.getText());
		this.appConfig.setProperty("server.port", this.portField.getText());
		this.appConfig.setProperty("server.ssl", Boolean.toString(this.sslCheckBox.isSelected()));
		this.appConfig.setProperty("user.id", this.idField.getText());
		this.appConfig.setProperty("user.password", this.passwordField.getText());
		this.appConfig.setProperty("server.authOption", this.authOptionBox.getSelectedItem().toString());
	}
}
