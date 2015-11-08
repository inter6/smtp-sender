package com.inter6.mail.gui;

import com.inter6.mail.gui.action.ActionPanel;
import com.inter6.mail.gui.data.DataPanel;
import com.inter6.mail.gui.setting.ServerPanel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.BorderLayout;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TabPanel extends TabComponentPanel {

	public TabPanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	public void init() {
		this.setLayout(new BorderLayout());
		this.add(tabComponentService.getTabComponent(tabName, ServerPanel.class), BorderLayout.NORTH);
		this.add(tabComponentService.getTabComponent(tabName, DataPanel.class), BorderLayout.CENTER);
		this.add(tabComponentService.getTabComponent(tabName, ActionPanel.class), BorderLayout.SOUTH);
	}
}
