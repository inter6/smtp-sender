package com.inter6.mail.gui.tab;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.ActionPanel;
import com.inter6.mail.gui.data.DataPanel;
import com.inter6.mail.gui.setting.ServerPanel;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TabPanel extends TabComponentPanel {

	public TabPanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	public void init() {
		this.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		{
			mainPanel.add(tabComponentManager.getTabComponent(tabName, ServerPanel.class));
			mainPanel.add(tabComponentManager.getTabComponent(tabName, DataPanel.class));
		}
		this.add(new JScrollPane(mainPanel), BorderLayout.CENTER);

		this.add(tabComponentManager.getTabComponent(tabName, ActionPanel.class), BorderLayout.SOUTH);
	}
}
