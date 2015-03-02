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
import com.inter6.mail.job.smtp.SmtpSendJobObserver;

@Component
public class ActionPanel extends JPanel implements SmtpSendJobObserver {
	private static final long serialVersionUID = -1786161460680791823L;

	@Autowired
	private DataPanel dataPanel;

	@Autowired
	private LogPanel logPanel;

	private final JButton startButton = new JButton("Start");
	private final JButton stopButton = new JButton("Stop");
	private final JProgressBar progressBar = new JProgressBar();
	private final JLabel progressLabel = new JLabel("--%");

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
			this.progressBar.setMaximum(100);
			progressPanel.add(this.progressBar, BorderLayout.CENTER);
			progressPanel.add(this.progressLabel, BorderLayout.EAST);
		}
		this.add(progressPanel);

		this.add(this.logPanel);
	}

	private final ActionListener startEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				ActionPanel.this.currentJob = ActionPanel.this.dataPanel.getSendJob();
				new Thread(ActionPanel.this.currentJob).start();
			} catch (Throwable e) {
				ActionPanel.this.logPanel.error("job build fail !", e);
			}
		}
	};

	@Override
	public void onStart(long startTime) {
		// TODO 진행율 초기화
		ActionPanel.this.startButton.setEnabled(false);
		ActionPanel.this.stopButton.setEnabled(true);
	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onError(Throwable e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDone(long startTime, long elapsedTime) {
		ActionPanel.this.startButton.setEnabled(true);
		ActionPanel.this.stopButton.setEnabled(false);
	}

	@Override
	public void onProgress(float progressRate) {
		this.progressBar.setValue((int) progressRate);
		this.progressLabel.setText(String.format("%.2f", progressRate) + "%");
	}
}
