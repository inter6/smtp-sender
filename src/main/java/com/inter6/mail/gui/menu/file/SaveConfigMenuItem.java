package com.inter6.mail.gui.menu.file;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.menu.TopMenuBar;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.TabComponentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

@Component
public class SaveConfigMenuItem extends JMenuItem implements ActionListener {

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private TopMenuBar topMenuBar;

	private TabComponentManager tabComponentManager = TabComponentManager.getInstance();

	private LogPanel logPanel;

	@PostConstruct
	private void init() { // NOPMD
		logPanel = tabComponentManager.getActiveTabComponent(LogPanel.class);

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
			for (ConfigObserver observer : tabComponentManager.getTabComponents(ConfigObserver.class)) {
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
