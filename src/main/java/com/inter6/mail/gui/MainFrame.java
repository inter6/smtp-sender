package com.inter6.mail.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.menu.TopMenuBar;
import com.inter6.mail.gui.menu.file.LoadConfigMenuItem;
import com.inter6.mail.gui.tab.RootTabPanel;
import com.inter6.mail.module.ModuleService;

@Component
@Slf4j
public class MainFrame extends JFrame {
	private static final long serialVersionUID = -6461973454673997240L;

	@Autowired
	private TopMenuBar topMenuBar;

	@Autowired
	private RootTabPanel rootTabPanel;

	@Autowired
	private LoadConfigMenuItem loadConfigMenuItem;

	public void execute() {
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.setTitle("smtp-sender v1.2.1 (released: 15/12/05) - inter6.com");
		this.setSize(1000, 800);
		this.setMinimumSize(new Dimension(600, 400));
		this.initLayout();
		this.setVisible(true);

		this.loadDefaultConfig();
	}

	private void initLayout() {
		this.setLayout(new BorderLayout());
		this.setJMenuBar(this.topMenuBar);
		this.add(this.rootTabPanel, BorderLayout.CENTER);
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
