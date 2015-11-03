package com.inter6.mail.gui;

import com.inter6.mail.gui.action.ActionPanel;
import com.inter6.mail.gui.data.DataPanel;
import com.inter6.mail.gui.menu.TopMenuBar;
import com.inter6.mail.gui.setting.SettingPanel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Slf4j
public class MainFrame extends JFrame {
	private static final long serialVersionUID = -6461973454673997240L;

	@Autowired
	private TopMenuBar topMenuBar;

	@Autowired
	private SettingPanel settingPanel;

	@Autowired
	private DataPanel dataPanel;

	/*@Autowired
	private AdvancedPanel advancedPanel;*/

	@Autowired
	private ActionPanel actionPanel;

	public void execute() {
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		/*try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.error("set look and feel fail !", e);
		}*/

		this.setTitle("smtp-sender v1.1.0 - inter6.com");
		this.setSize(1000, 800);
		this.setMinimumSize(new Dimension(600, 400));
		this.initLayout();
		this.setVisible(true);

		this.settingPanel.loadDefaultConfig();
	}

	private void initLayout() {
		this.setLayout(new BorderLayout());
		this.setJMenuBar(this.topMenuBar);
		this.add(this.settingPanel, BorderLayout.NORTH);
		this.add(this.dataPanel, BorderLayout.CENTER);
//		this.add(this.advancedPanel, BorderLayout.EAST);
		this.add(this.actionPanel, BorderLayout.SOUTH);
	}
}
