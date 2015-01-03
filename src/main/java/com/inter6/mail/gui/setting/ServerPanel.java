package com.inter6.mail.gui.setting;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.model.AuthOption;
import com.inter6.mail.model.setting.ServerData;
import com.inter6.mail.module.AppConfig;

@Component
public class ServerPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = -7540651743867028995L;

	@Autowired
	private AppConfig appConfig;

	private final JTextField hostFiled = new JTextField(40);
	private final JTextField portField = new JTextField("25", 5);
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

	public ServerData getServerData() {
		ServerData serverData = new ServerData();
		serverData.setHost(this.hostFiled.getText());
		serverData.setPort(this.portField.getText());
		serverData.setSsl(this.sslCheckBox.isSelected());
		serverData.setId(this.idField.getText());
		serverData.setPassword(this.passwordField.getText());
		serverData.setAuthOption((AuthOption) this.authOptionBox.getSelectedItem());
		return serverData;
	}

	@Override
	public void loadConfig() {
		ServerData serverData = new Gson().fromJson(this.appConfig.getUnsplitString("server.data"), ServerData.class);
		if (serverData == null) {
			return;
		}
		this.hostFiled.setText(serverData.getHost());
		this.portField.setText(Integer.toString(serverData.getPort()));
		this.sslCheckBox.setSelected(serverData.isSsl());
		this.idField.setText(serverData.getId());
		this.passwordField.setText(serverData.getPassword());
		this.authOptionBox.setSelectedIndex(serverData.getAuthOption().ordinal());
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("server.data", new Gson().toJson(this.getServerData()));
	}
}
