package com.inter6.mail.gui.data;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;

@Component
public class MimeSourcePanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = -3278717924684919247L;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JTextArea mimeArea = new JTextArea();
		JScrollPane mimeScrollPane = new JScrollPane(mimeArea);
		this.add(mimeScrollPane, BorderLayout.CENTER);
	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
