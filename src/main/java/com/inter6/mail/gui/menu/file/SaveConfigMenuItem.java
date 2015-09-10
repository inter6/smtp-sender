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
public class SaveConfigMenuItem extends JMenuItem implements ActionListener {

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private LogPanel logPanel;

	@Autowired
	private TopMenuBar topMenuBar;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("Save Config");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File configFile = this.appConfig.getFile();
		if (configFile == null) {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
				this.logPanel.info("save config cancel.");
				return;
			}
			configFile = fileChooser.getSelectedFile();
		}
		this.saveConfig(configFile);
	}

	public void saveConfig(File configFile) {
		try {
			Map<String, ConfigObserver> observers = ModuleService.getBeans(ConfigObserver.class);
			for (ConfigObserver observer : observers.values()) {
				observer.updateConfig();
			}

			this.appConfig.save(configFile, "UTF-8");
			this.appConfig.setFile(configFile);
			this.topMenuBar.setConfigPath(this.appConfig.getFileName());
			this.logPanel.info("save config - FILE:" + configFile);
		} catch (Throwable e) {
			this.logPanel.errorAndShowDialog("config save fail ! - FILE:" + this.appConfig.getFileName(), e);
		}
	}
}
