package com.inter6.mail.gui.menu.file;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.menu.TopMenuBar;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

@Component
public class LoadConfigMenuItem extends JMenuItem implements ActionListener {

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private LogPanel logPanel;

	@Autowired
	private TopMenuBar topMenuBar;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("Load Config");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		File configFile = this.appConfig.getFile();
		if (configFile != null && configFile.isFile()) {
			configFile = configFile.getParentFile();
		}
		JFileChooser fileChooser = new JFileChooser(configFile);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			this.loadConfig(fileChooser.getSelectedFile());
		}
	}

	public void loadConfig(File configFile) {
		try {
			this.appConfig.clear();
			this.appConfig.load(configFile, "UTF-8");

			Map<String, ConfigObserver> observers = ModuleService.getBeans(ConfigObserver.class);
			for (ConfigObserver observer : observers.values()) {
				observer.loadConfig();
			}

			this.appConfig.setFile(configFile);
			this.topMenuBar.setConfigPath(this.appConfig.getFileName());
			this.logPanel.info("load config - FILE:" + configFile);
		} catch (Throwable e) {
			this.appConfig.clear();
			this.logPanel.errorAndShowDialog("load config fail ! - FILE:" + configFile, e);
		}
	}
}
