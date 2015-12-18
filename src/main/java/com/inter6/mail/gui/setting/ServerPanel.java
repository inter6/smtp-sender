package com.inter6.mail.gui.setting;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.tab.TabComponentPanel;
import com.inter6.mail.model.AuthOption;
import com.inter6.mail.model.HeloType;
import com.inter6.mail.model.setting.ServerData;
import com.inter6.mail.module.AppConfig;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServerPanel extends TabComponentPanel implements ConfigObserver {
	private static final long serialVersionUID = -7540651743867028995L;

	@Autowired
	private AppConfig appConfig;

	private final JTextField hostField = new JTextField(25);
	private final JTextField portField = new JTextField("25", 5);
	private final JComboBox<String> connectTypeOptionBox = new JComboBox<>(new String[] { "NONE", "SSL", "TLS" });
	private final JComboBox<HeloType> heloTypeBox = new JComboBox<>(HeloType.allItems());
	private final JTextField heloDomainField = new JTextField(25);
	private final JTextField idField = new JTextField(15);
	private final JPasswordField passwordField = new JPasswordField(15);
	private final JComboBox<AuthOption> authOptionBox = new JComboBox<>(AuthOption.allItems());

	public ServerPanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	private void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel hostPanel = new JPanel(new FlowLayout());
		{
			hostPanel.add(new JLabel("Host"));
			hostPanel.add(this.hostField);
			hostPanel.add(new JLabel("Port"));
			hostPanel.add(this.portField);
			hostPanel.add(this.connectTypeOptionBox);
		}
		this.add(hostPanel);

		JPanel heloPanel = new JPanel(new FlowLayout());
		{
			heloPanel.add(new JLabel("HELO"));
			heloPanel.add(this.heloDomainField);

			this.heloTypeBox.addActionListener(this.createHeloChangeEvent());
			this.heloTypeBox.setSelectedIndex(0);
			heloPanel.add(this.heloTypeBox);
		}
		this.add(heloPanel);

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
		this.add(accountPanel);
	}

	private ActionListener createHeloChangeEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HeloType heloType = (HeloType) ServerPanel.this.heloTypeBox.getSelectedItem();
				ServerPanel.this.heloDomainField.setEnabled(heloType != HeloType.NONE);
			}
		};
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
		serverData.setHeloType((HeloType) this.heloTypeBox.getSelectedItem());
		serverData.setHeloDomain(this.heloDomainField.getText());
		serverData.setId(this.idField.getText());
		serverData.setPassword(this.passwordField.getText());
		serverData.setAuthOption((AuthOption) this.authOptionBox.getSelectedItem());
		return serverData;
	}

	@Override
	public void loadConfig() {
		ServerData serverData = new Gson().fromJson(this.appConfig.getString(tabName + ".server.data"), ServerData.class);
		if (serverData == null) {
			return;
		}
		this.hostField.setText(serverData.getHost());
		this.portField.setText(Integer.toString(serverData.getPort()));
		this.connectTypeOptionBox.setSelectedItem(serverData.getConnectType());

		HeloType heloType = serverData.getHeloType();
		if (heloType != null) {
			this.heloTypeBox.setSelectedIndex(heloType.ordinal());
		}

		this.heloDomainField.setText(serverData.getHeloDomain());
		this.idField.setText(serverData.getId());
		this.passwordField.setText(serverData.getPassword());
		this.authOptionBox.setSelectedIndex(serverData.getAuthOption().ordinal());
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty(tabName + ".server.data", new Gson().toJson(this.getServerData()));
	}
}
