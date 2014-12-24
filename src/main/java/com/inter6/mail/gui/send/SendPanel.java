package com.inter6.mail.gui.send;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.stereotype.Component;

@Component
public class SendPanel extends JPanel {
	private static final long serialVersionUID = -1786161460680791823L;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel actionPanel = new JPanel(new FlowLayout());
		{
			JButton startButton = new JButton("Start");
			JButton stopButton = new JButton("Stop");
			actionPanel.add(startButton);
			actionPanel.add(stopButton);
		}
		this.add(actionPanel, BorderLayout.NORTH);

		JTextArea logArea = new JTextArea();
		JScrollPane logScrollPane = new JScrollPane(logArea);
		this.add(logScrollPane, BorderLayout.CENTER);
	}
}
