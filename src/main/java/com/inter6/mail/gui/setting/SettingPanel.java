package com.inter6.mail.gui.setting;

import com.inter6.mail.gui.menu.file.LoadConfigMenuItem;
import com.inter6.mail.module.ModuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

@Component
public class SettingPanel extends JPanel {
	private static final long serialVersionUID = 4353721392394920182L;

	@Autowired
	private ServerPanel serverPanel;

	@Autowired
	private LoadConfigMenuItem loadConfigMenuItem;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());
		this.add(this.serverPanel, BorderLayout.CENTER);
	}

	public void loadDefaultConfig() {
		File configFile = this.getConfigFile();
		if (configFile != null && configFile.isFile()) {
			this.loadConfigMenuItem.loadConfig(configFile);
		}
	}

	private File getConfigFile() {
		String configPath = System.getProperty("app.config");
		if (StringUtils.isNotBlank(configPath)) {
			return new File(configPath);
		}

		if (StringUtils.equalsIgnoreCase(System.getProperty("test"), "true")) {
			try {
				return ModuleService.getContext().getResource("app.config").getFile();
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}
}
