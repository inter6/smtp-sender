package com.inter6.mail.gui;

import com.inter6.mail.service.TabComponentService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

@Component
public class RootTabPanel extends JTabbedPane {

	@Autowired
	private TabComponentService tabComponentService;

	@Getter
	private String activeTabName = "Default";

	@PostConstruct
	private void init() {
		this.addTab("Default", tabComponentService.getTabComponent("Default", TabPanel.class));
	}
}
