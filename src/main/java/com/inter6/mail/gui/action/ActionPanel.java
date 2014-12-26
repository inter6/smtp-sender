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

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel sendPanel = new JPanel(new FlowLayout());
		{
			JButton startButton = new JButton("Start");
			JButton stopButton = new JButton("Stop");
			startButton.addActionListener(this.startEvent);
			sendPanel.add(startButton);
			sendPanel.add(stopButton);

			// XXX 구현되면 제거
			stopButton.setEnabled(false);
		}
		this.add(sendPanel, BorderLayout.NORTH);
		this.add(this.logPanel, BorderLayout.CENTER);
	}

	private final ActionListener startEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ev) {
			// TODO 쓰레드 처리
			ActionPanel.this.dataPanel.getSendJob().execute();
		}
	};
}
