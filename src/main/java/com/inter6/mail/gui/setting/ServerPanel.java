package com.inter6.mail.gui.setting;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;

@Component
public class ServerPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = -7540651743867028995L;

	private final JTextField hostFiled = new JTextField(20);
	private final JTextField portField = new JTextField(5);
	private final JCheckBox sslCheckBox = new JCheckBox("SSL");
	private final JTextField idField = new JTextField(10);
	private final JTextField passwordField = new JTextField(10);
	private JComboBox<String> loginComboBox;

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
			accountPanel.add(this.getLoginComboBox());
		}
		this.add(accountPanel, BorderLayout.CENTER);
	}

	private JComboBox<String> getLoginComboBox() {
		if (this.loginComboBox == null) {
			this.loginComboBox = new JComboBox<String>(new String[] { "a", "b" });
		}
		return this.loginComboBox;
	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
