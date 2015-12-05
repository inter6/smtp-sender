package com.inter6.mail.gui.data;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.component.DatePanel;
import com.inter6.mail.gui.tab.TabComponentPanel;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.job.smtp.ScpSmtpSendJob;
import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.data.ScpSourceData;
import com.inter6.mail.module.AppConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScpSourcePanel extends TabComponentPanel implements SendJobBuilder, ConfigObserver {

	@Autowired
	private AppConfig appConfig;

	private final JTextField hostField = new JTextField(25);
	private final JTextField portField = new JTextField("22", 5);
	private final JTextField usernameField = new JTextField(15);
	private final JPasswordField passwordField = new JPasswordField(15);

	private final JTextField pathField = new JTextField("/opt/eml/*.eml", 30);
	private final DefaultListModel<String> pathListModel = new DefaultListModel<>();
	private final JList<String> pathList = new JList<>(this.pathListModel);
	private final DatePanel replaceDatePanel = new DatePanel("Replace Date", 20, false, true);

	public ScpSourcePanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	private void init() {
		this.setLayout(new BorderLayout());

		JPanel settingPanel = new JPanel();
		settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));
		{
			JPanel hostPanel = new JPanel(new FlowLayout());
			{
				hostPanel.add(new JLabel("Host"));
				hostPanel.add(this.hostField);
				hostPanel.add(new JLabel("Port"));
				hostPanel.add(this.portField);
			}
			settingPanel.add(hostPanel);

			JPanel accountPanel = new JPanel(new FlowLayout());
			{
				accountPanel.add(new JLabel("Username"));
				accountPanel.add(this.usernameField);
				accountPanel.add(new JLabel("Password"));
				accountPanel.add(this.passwordField);
			}
			settingPanel.add(accountPanel);
		}
		this.add(settingPanel, BorderLayout.NORTH);

		JPanel listPanel = new JPanel(new BorderLayout());
		{
			JPanel addPanel = new JPanel(new FlowLayout());
			{
				addPanel.add(new JLabel("Path"));
				addPanel.add(this.pathField);

				JButton addButton = new JButton("Add");
				addButton.addActionListener(this.createAddEvent());
				addPanel.add(addButton);
			}
			listPanel.add(addPanel, BorderLayout.NORTH);

			listPanel.add(pathList, BorderLayout.CENTER);

			JPanel actionPanel = new JPanel();
			actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
			{
				JButton removeButton = new JButton("Remove");
				JButton dedupAndSortButton = new JButton("Dedup&Sort");
				removeButton.addActionListener(this.createRemoveEvent());
				dedupAndSortButton.addActionListener(this.createDedupAndSortEvent());
				actionPanel.add(removeButton);
				actionPanel.add(dedupAndSortButton);
			}
			listPanel.add(actionPanel, BorderLayout.EAST);
		}
		this.add(listPanel, BorderLayout.CENTER);

		this.add(replaceDatePanel, BorderLayout.SOUTH);
	}

	private ActionListener createAddEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ScpSourcePanel.this.pathListModel.addElement(pathField.getText());
			}
		};
	}

	private ActionListener createRemoveEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (String path : ScpSourcePanel.this.pathList.getSelectedValuesList()) {
					ScpSourcePanel.this.pathListModel.removeElement(path);
				}
			}
		};
	}

	private ActionListener createDedupAndSortEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ScpSourcePanel.this.pathListModel.isEmpty()) {
					return;
				}
				Set<String> paths = new TreeSet<>();
				for (int i = 0; i < ScpSourcePanel.this.pathListModel.size(); i++) {
					paths.add(ScpSourcePanel.this.pathListModel.get(i));
				}
				ScpSourcePanel.this.pathListModel.clear();
				for (String path : paths) {
					ScpSourcePanel.this.pathListModel.addElement(path);
				}
			}
		};
	}

	@Override
	public AbstractSmtpSendJob buildSendJob() throws Throwable {
		ScpSmtpSendJob scpSmtpSendJob = tabComponentManager.getTabComponent(tabName, ScpSmtpSendJob.class);
		scpSmtpSendJob.setScpSourceData(this.getScpSourceData());
		return scpSmtpSendJob;
	}

	private ScpSourceData getScpSourceData() {
		ScpSourceData scpSourceData = new ScpSourceData();
		scpSourceData.setHost(hostField.getText());

		String port = portField.getText();
		if (!NumberUtils.isDigits(port)) {
			throw new IllegalArgumentException("port is not digits ! - PORT:" + port);
		}
		scpSourceData.setPort(Integer.parseInt(port));

		scpSourceData.setUsername(usernameField.getText());
		scpSourceData.setPassword(passwordField.getText());

		List<String> paths = new ArrayList<>();
		for (int i = 0; i < this.pathListModel.size(); i++) {
			paths.add(this.pathListModel.get(i));
		}
		scpSourceData.setPaths(paths);
		scpSourceData.setReplaceDateData(this.replaceDatePanel.getDateData());
		return scpSourceData;
	}

	@Override
	public void loadConfig() {
		this.pathListModel.clear();
		ScpSourceData scpSourceData = new Gson().fromJson(this.appConfig.getString(tabName + ".scp.source.data"), ScpSourceData.class);
		if (scpSourceData == null) {
			return;
		}

		hostField.setText(scpSourceData.getHost());
		portField.setText(scpSourceData.getPort() + "");
		usernameField.setText(scpSourceData.getUsername());
		passwordField.setText(scpSourceData.getPassword());

		Collection<String> paths = scpSourceData.getPaths();
		if (CollectionUtils.isNotEmpty(paths)) {
			for (String path : paths) {
				this.pathListModel.addElement(path);
			}
		}

		DateData replaceDateData = scpSourceData.getReplaceDateData();
		if (replaceDateData != null) {
			this.replaceDatePanel.setDateData(replaceDateData);
		}
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty(tabName + ".scp.source.data", new Gson().toJson(this.getScpSourceData()));
	}
}
