package com.inter6.mail.gui;

import com.inter6.mail.service.TabComponentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.JPanel;

public class TabComponentPanel extends JPanel implements TabComponent {

	protected String tabName;

	@Autowired
	protected TabComponentService tabComponentService;

	public TabComponentPanel(String tabName) {
		this.tabName = tabName;
	}
}
