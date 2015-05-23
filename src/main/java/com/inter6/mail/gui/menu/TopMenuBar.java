package com.inter6.mail.gui.menu;

import javax.annotation.PostConstruct;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopMenuBar extends JMenuBar {
	private static final long serialVersionUID = -3782465535113858483L;

	@Autowired
	private Base64MenuItem base64MenuItem;

	@Autowired
	private Rfc2074MenuItem rfc2074MenuItem;

	@Autowired
	private DnsMenuItem dnsMenuItem;

	@Autowired
	private AboutMenuItem aboutMenuItem;

	@PostConstruct
	private void init() { // NOPMD
		JMenu toolMenu = new JMenu("Tools");
		{
			toolMenu.add(this.base64MenuItem);
			toolMenu.add(this.rfc2074MenuItem);
			toolMenu.add(this.dnsMenuItem);
		}
		this.add(toolMenu);

		JMenu helpMenu = new JMenu("Help");
		{
			helpMenu.add(this.aboutMenuItem);
		}
		this.add(helpMenu);
	}
}
