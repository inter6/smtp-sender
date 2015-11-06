package com.inter6.mail.gui;

import com.inter6.mail.gui.action.ActionPanel;
import com.inter6.mail.gui.data.DataPanel;
import com.inter6.mail.gui.menu.TopMenuBar;
import com.inter6.mail.gui.menu.file.LoadConfigMenuItem;
import com.inter6.mail.gui.setting.ServerPanel;
import com.inter6.mail.module.ModuleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class MainFrame extends JFrame {
	private static final long serialVersionUID = -6461973454673997240L;

	@Autowired
	private TopMenuBar topMenuBar;

	@Autowired
	private ServerPanel serverPanel;

	@Autowired
	private DataPanel dataPanel;

	@Autowired
	private ActionPanel actionPanel;

	@Autowired
	private LoadConfigMenuItem loadConfigMenuItem;

	public void execute() {
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.setTitle("smtp-sender v1.1.1 (released: 15/11/07) - inter6.com");
		this.setSize(1000, 800);
		this.setMinimumSize(new Dimension(600, 400));
		this.initLayout();
		this.setVisible(true);

		this.loadDefaultConfig();
	}

	private void initLayout() {
		this.setLayout(new BorderLayout());
		this.setJMenuBar(this.topMenuBar);
		this.add(this.serverPanel, BorderLayout.NORTH);
		this.add(this.dataPanel, BorderLayout.CENTER);
		this.add(this.actionPanel, BorderLayout.SOUTH);
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
