package com.inter6.mail.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.JMenuItem;

import org.springframework.stereotype.Component;

@Component
public class DnsMenuItem extends JMenuItem implements ActionListener {
	private static final long serialVersionUID = 3869102798552221510L;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("DNS Query");
		this.addActionListener(this);

		// TODO 구현되면 제거
		this.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
