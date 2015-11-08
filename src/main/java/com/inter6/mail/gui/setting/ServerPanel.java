package com.inter6.mail.gui.setting;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.model.AuthOption;
import com.inter6.mail.model.setting.ServerData;
import com.inter6.mail.module.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class ServerPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = -7540651743867028995L;

	@Autowired
	private AppConfig appConfig;

	private final JTextField hostField = new JTextField(25);
	private final JTextField portField = new JTextField("25", 5);
	private final JComboBox<String> connectTypeOptionBox = new JComboBox<>(new String[]{"NONE", "SSL", "TLS"});
	private final JTextField idField = new JTextField(15);
	private final JPasswordField passwordField = new JPasswordField(15);
	private final JComboBox<AuthOption> authOptionBox = new JComboBox<>(AuthOption.allItems());

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel hostPanel = new JPanel(new FlowLayout());
		{
			hostPanel.add(new JLabel("Host"));
			hostPanel.add(this.hostField);
			hostPanel.add(new JLabel("Port"));
			hostPanel.add(this.portField);
			hostPanel.add(this.connectTypeOptionBox);
		}
		this.add(hostPanel, BorderLayout.NORTH);

		JPanel accountPanel = new JPanel(new FlowLayout());
		{
			accountPanel.add(new JLabel("ID"));
			accountPanel.add(this.idField);
			accountPanel.add(new JLabel("PW"));
			accountPanel.add(this.passwordField);

			this.authOptionBox.addActionListener(this.createAuthChangeEvent());
			this.authOptionBox.setSelectedIndex(0);
			accountPanel.add(this.authOptionBox);
		}
		this.add(accountPanel, BorderLayout.CENTER);
	}

	private ActionListener createAuthChangeEvent() {
		return new ActionListener() {

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
	}

	private void setEnableAccountField(boolean isEnable) {
		this.idField.setEnabled(isEnable);
		this.idField.setEditable(isEnable);
		this.passwordField.setEnabled(isEnable);
		this.passwordField.setEditable(isEnable);
	}

	public ServerData getServerData() {
		ServerData serverData = new ServerData();
		serverData.setHost(this.hostField.getText());
		serverData.setPort(this.portField.getText());
		serverData.setConnectType((String) this.connectTypeOptionBox.getSelectedItem());
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
		this.hostField.setText(serverData.getHost());
		this.portField.setText(Integer.toString(serverData.getPort()));
		this.connectTypeOptionBox.setSelectedItem(serverData.getConnectType());
		this.idField.setText(serverData.getId());
		this.passwordField.setText(serverData.getPassword());
		this.authOptionBox.setSelectedIndex(serverData.getAuthOption().ordinal());
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("server.data", new Gson().toJson(this.getServerData()));
	}
}
