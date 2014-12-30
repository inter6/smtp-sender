package com.inter6.mail.gui.menu;

import javax.annotation.PostConstruct;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.springframework.stereotype.Component;

@Component
public class TopMenuBar extends JMenuBar {
	private static final long serialVersionUID = -3782465535113858483L;

	@PostConstruct
	private void init() { // NOPMD
		JMenu toolMenu = new JMenu("Tools");
		{
			JMenuItem base64Item = new JMenuItem("Base64 Encoder/Decoder");
			toolMenu.add(base64Item);

			// XXX 구현되면 제거
			base64Item.setEnabled(false);

			JMenuItem rfc2047Item = new JMenuItem("RFC2047 Encoder/Decoder");
			toolMenu.add(rfc2047Item);

			// XXX 구현되면 제거
			rfc2047Item.setEnabled(false);

			JMenuItem dnsItem = new JMenuItem("DNS Query");
			toolMenu.add(dnsItem);

			// XXX 구현되면 제거
			dnsItem.setEnabled(false);
		}
		this.add(toolMenu);
	}
}
