package com.inter6.mail.gui.action;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.data.DataPanel;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;

@Component
public class ActionPanel extends JPanel {
	private static final long serialVersionUID = -1786161460680791823L;

	@Autowired
	private DataPanel dataPanel;

	@Autowired
	private LogPanel logPanel;

	private final JButton startButton = new JButton("Start");
	private final JButton stopButton = new JButton("Stop");
	private final JProgressBar progressBar = new JProgressBar();

	private AbstractSmtpSendJob currentJob;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel sendPanel = new JPanel(new FlowLayout());
		{
			this.startButton.addActionListener(this.startEvent);
			sendPanel.add(this.startButton);
			sendPanel.add(this.stopButton);
			this.stopButton.setEnabled(false);
		}
		this.add(sendPanel);

		JPanel progressPanel = new JPanel(new BorderLayout());
		{
			progressPanel.add(new JLabel("--:--:--"), BorderLayout.WEST);
			this.progressBar.setBorder(new EmptyBorder(0, 10, 0, 10));
			progressPanel.add(this.progressBar, BorderLayout.CENTER);
			progressPanel.add(new JLabel("---%"), BorderLayout.EAST);
		}
		this.add(progressPanel);

		this.add(this.logPanel);
	}

	private final ActionListener startEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ev) {
			String jobName = "unknown";
			try {
				ActionPanel.this.currentJob = ActionPanel.this.dataPanel.getSendJob();
				jobName = ActionPanel.this.currentJob.getClass().getSimpleName();
				ActionPanel.this.startButton.setEnabled(false);
				ActionPanel.this.stopButton.setEnabled(true);
				ActionPanel.this.currentJob.execute();
				ActionPanel.this.logPanel.info("job done - JOB:" + jobName);
			} catch (Throwable e) {
				ActionPanel.this.logPanel.error("job fail ! - JOB:" + jobName, e);
			} finally {
				ActionPanel.this.startButton.setEnabled(true);
				ActionPanel.this.stopButton.setEnabled(false);
			}
		}
	};
}
