package com.inter6.mail.gui.advanced;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdvancedPanel extends JPanel {
	private static final long serialVersionUID = 44439027876264289L;

	@Autowired
	private PreSendSettingPanel preSendSettingPanel;

	@Autowired
	private PostSendSettingPanel postSendSettingPanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
		{
			wrapPanel.add(this.preSendSettingPanel);
			wrapPanel.add(this.postSendSettingPanel);
		}
		JScrollPane wrapScrollPane = new JScrollPane(wrapPanel);
		wrapScrollPane.setPreferredSize(new Dimension(400, 0));
		this.add(wrapScrollPane, BorderLayout.CENTER);
	}
}
