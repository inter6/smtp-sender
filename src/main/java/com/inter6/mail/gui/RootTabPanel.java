package com.inter6.mail.gui;

import com.inter6.mail.module.TabComponentManager;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

@Component
public class RootTabPanel extends JTabbedPane {

	private TabComponentManager tabComponentManager = TabComponentManager.getInstance();

	@Getter
	private String activeTabName = "Default";

	@PostConstruct
	private void init() {
		this.addTab("Default", tabComponentManager.getTabComponent("Default", TabPanel.class));
	}
}
