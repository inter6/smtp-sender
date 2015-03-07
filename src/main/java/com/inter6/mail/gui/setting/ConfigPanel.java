package com.inter6.mail.gui.setting;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;

@Component
public class ConfigPanel extends JPanel {
	private static final long serialVersionUID = 6971507331511881692L;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private LogPanel logPanel;

	private final JTextField pathField = new JTextField(40);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new FlowLayout());

		this.add(new JLabel("Config"));

		this.pathField.setEditable(false);
		this.add(this.pathField);

		JPanel actionPanel = new JPanel(new FlowLayout());
		{
			JButton loadButton = new JButton("Load");
			JButton saveButton = new JButton("Save");
			loadButton.addActionListener(this.loadEvent);
			saveButton.addActionListener(this.saveEvent);
			actionPanel.add(loadButton);
			actionPanel.add(saveButton);
		}
		this.add(actionPanel);
	}

	private final ActionListener loadEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ev) {
			File configFile = ConfigPanel.this.appConfig.getFile();
			if (configFile != null && configFile.isFile()) {
				configFile = configFile.getParentFile();
			}
			JFileChooser fileChooser = new JFileChooser(configFile);
			fileChooser.setFileFilter(new FileNameExtensionFilter("*.config", "config"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fileChooser.showOpenDialog(ConfigPanel.this) == JFileChooser.APPROVE_OPTION) {
				ConfigPanel.this.loadConfig(fileChooser.getSelectedFile());
			}
		}
	};

	public void loadConfig(File configFile) {
		try {
			this.appConfig.clear();
			this.appConfig.load(configFile, "UTF-8");

			Map<String, ConfigObserver> observers = ModuleService.getBeans(ConfigObserver.class);
			for (ConfigObserver observer : observers.values()) {
				observer.loadConfig();
				if (observer instanceof JComponent) {
					((JComponent) observer).validate();
				}
			}

			this.appConfig.setFile(configFile);
			this.pathField.setText(this.appConfig.getFileName());
			this.logPanel.info("load config - FILE:" + configFile);
		} catch (Throwable e) {
			this.appConfig.clear();
			this.logPanel.errorAndShowDialog("load config fail ! - FILE:" + configFile, e);
		}
	}

	private final ActionListener saveEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ev) {
			File configFile = ConfigPanel.this.appConfig.getFile();
			if (configFile == null) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(ConfigPanel.this) != JFileChooser.APPROVE_OPTION) {
					ConfigPanel.this.logPanel.info("save config cancle");
					return;
				}
				configFile = fileChooser.getSelectedFile();
			}

			ConfigPanel.this.saveConfig(configFile);
		}
	};

	private void saveConfig(File configFile) {
		try {
			Map<String, ConfigObserver> observers = ModuleService.getBeans(ConfigObserver.class);
			for (ConfigObserver observer : observers.values()) {
				observer.updateConfig();
			}

			this.appConfig.save(configFile, "UTF-8");
			this.appConfig.setFile(configFile);
			this.pathField.setText(this.appConfig.getFileName());
			this.logPanel.info("save config - FILE:" + configFile);
		} catch (Throwable e) {
			this.logPanel.errorAndShowDialog("config save fail ! - FILE:" + this.appConfig.getFileName(), e);
		}
	}
}
