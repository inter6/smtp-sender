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
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.service.AuthOption;

@Component
public class ServerPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = -7540651743867028995L;

	private final JTextField hostFiled = new JTextField(20);
	private final JTextField portField = new JTextField(5);
	private final JCheckBox sslCheckBox = new JCheckBox("SSL");
	private final JTextField idField = new JTextField(10);
	private final JTextField passwordField = new JTextField(10);
	private JComboBox<AuthOption> authOptionBox;

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
			accountPanel.add(this.getAuthComboBox());
		}
		this.add(accountPanel, BorderLayout.CENTER);
	}

	private JComboBox<AuthOption> getAuthComboBox() {
		if (this.authOptionBox == null) {
			this.authOptionBox = new JComboBox<AuthOption>(AuthOption.allItems());
			this.authOptionBox.addActionListener(this.authChangeEvent);
			this.authOptionBox.setSelectedIndex(0);
		}
		return this.authOptionBox;
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
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
