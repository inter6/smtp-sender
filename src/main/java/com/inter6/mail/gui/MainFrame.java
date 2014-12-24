package com.inter6.mail.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.data.DataPanel;
import com.inter6.mail.gui.send.SendPanel;
import com.inter6.mail.gui.setting.SettingPanel;

@Component
public class MainFrame extends JFrame {
	private static final long serialVersionUID = -6461973454673997240L;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SettingPanel settingPanel;

	@Autowired
	private DataPanel dataPanel;

	@Autowired
	private SendPanel sendPanel;

	public void execute() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			this.log.error("set look and feel fail !", e);
		}
		this.setSize(600, 800);
		this.setMinimumSize(new Dimension(500, 600));

		this.initLayout();
		this.setVisible(true);
	}

	private void initLayout() {
		this.setLayout(new BorderLayout());
		this.add(this.settingPanel, BorderLayout.NORTH);
		this.add(this.dataPanel, BorderLayout.CENTER);
		this.add(this.sendPanel, BorderLayout.SOUTH);
	}
}
