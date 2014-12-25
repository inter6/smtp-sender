package com.inter6.mail.gui.setting;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;

@Component
public class ConfigPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 6971507331511881692L;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AppConfig appConfig;

	private final JTextField pathField = new JTextField(20);

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
			File prevConfigFile = ConfigPanel.this.appConfig.getFile();
			if (prevConfigFile != null && prevConfigFile.isFile()) {
				prevConfigFile = prevConfigFile.getParentFile();
			}
			JFileChooser fileChooser = new JFileChooser(prevConfigFile);
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
			this.appConfig.load(configFile);
			this.appConfig.setFile(configFile);
			this.log.info("load config - FILE:" + configFile);

			Map<String, ConfigObserver> observers = ModuleService.getBeans(ConfigObserver.class);
			for (ConfigObserver observer : observers.values()) {
				observer.updateConfig();
			}
		} catch (ConfigurationException e) {
			this.appConfig.clear();
			this.appConfig.setFile(null);
			this.pathField.setText("");
			String msg = "load config fail ! - FILE:" + configFile + " ERR:" + e.getMessage();
			this.log.error(msg, e);
			JOptionPane.showMessageDialog(this, msg);
		}
	}

	private final ActionListener saveEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				ConfigPanel.this.appConfig.save();
			} catch (ConfigurationException e) {
				String msg = "config save fail ! - FILE:" + ConfigPanel.this.appConfig.getFileName() + " ERR:" + e.getMessage();
				ConfigPanel.this.log.error(msg, e);
				JOptionPane.showMessageDialog(ConfigPanel.this, msg);
			}
		}
	};

	@Override
	public void updateConfig() {
		this.pathField.setText(this.appConfig.getFileName());
	}
}
