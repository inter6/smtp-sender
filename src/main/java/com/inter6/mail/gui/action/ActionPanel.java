package com.inter6.mail.gui.action;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.data.DataPanel;

@Component
public class ActionPanel extends JPanel {
	private static final long serialVersionUID = -1786161460680791823L;

	@Autowired
	private DataPanel dataPanel;

	@Autowired
	private LogPanel logPanel;

	private final JButton startButton = new JButton("Start");
	private final JButton stopButton = new JButton("Stop");

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel sendPanel = new JPanel(new FlowLayout());
		{
			this.startButton.addActionListener(this.startEvent);
			sendPanel.add(this.startButton);
			sendPanel.add(this.stopButton);

			// XXX 구현되면 제거
			this.stopButton.setEnabled(false);
		}
		this.add(sendPanel, BorderLayout.NORTH);
		this.add(this.logPanel, BorderLayout.CENTER);
	}

	private final ActionListener startEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ev) {
			// TODO 쓰레드 처리
			try {
				ActionPanel.this.startButton.setEnabled(false);
				// XXX 구현되면 주석 제거
				//				ActionPanel.this.stopButton.setEnabled(true);

				ActionPanel.this.dataPanel.getSendJob().execute();
			} finally {
				ActionPanel.this.startButton.setEnabled(true);
				ActionPanel.this.stopButton.setEnabled(false);
			}
		}
	};
}
