package com.inter6.mail.gui.action;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.TabComponentPanel;
import com.inter6.mail.gui.data.DataPanel;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.model.action.ActionData;
import com.inter6.mail.module.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActionPanel extends TabComponentPanel implements ConfigObserver {
	private static final long serialVersionUID = -1786161460680791823L;

	@Autowired
	private AppConfig appConfig;

	private LogPanel logPanel;
	private DataPanel dataPanel;

	private final JButton startButton = new JButton("Start");
	private final JButton stopButton = new JButton("Stop");
	private final JCheckBox useMultiThreadCheckBox = new JCheckBox("Multi-Thread", true);
	private final JTextField maxThreadCountField = new JTextField("8", 2);
	private final JLabel elapsedTimeLabel = new JLabel("--:--:--");
	private final JProgressBar progressBar = new JProgressBar();
	private final JLabel progressLabel = new JLabel("0.00 %");

	private AbstractSmtpSendJob currentJob;

	public ActionPanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	private void init() { // NOPMD
		logPanel = tabComponentManager.getTabComponent(tabName, LogPanel.class);
		dataPanel = tabComponentManager.getTabComponent(tabName, DataPanel.class);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel sendPanel = new JPanel(new FlowLayout());
		{
			sendPanel.add(this.startButton);
			sendPanel.add(this.stopButton);
			sendPanel.add(this.useMultiThreadCheckBox);
			sendPanel.add(new JLabel("Max:"));
			sendPanel.add(this.maxThreadCountField);

			this.startButton.addActionListener(this.createStartEvent());
			this.stopButton.addActionListener(this.createStopEvent());
			this.stopButton.setEnabled(false);
		}
		this.add(sendPanel);

		JPanel progressPanel = new JPanel(new BorderLayout());
		{
			progressPanel.add(this.elapsedTimeLabel, BorderLayout.WEST);
			{
				this.progressBar.add(new JLabel("12345"));

				this.progressBar.setBorder(new EmptyBorder(0, 10, 0, 10));
				this.progressBar.setMaximum(100);
			}
			progressPanel.add(this.progressBar, BorderLayout.CENTER);
			progressPanel.add(this.progressLabel, BorderLayout.EAST);
		}
		this.add(progressPanel);

		this.add(this.logPanel);
	}

	private ActionListener createStartEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					ActionPanel.this.currentJob = dataPanel.getSendJob();
					new Thread(ActionPanel.this.currentJob).start();
				} catch (Throwable e) {
					ActionPanel.this.logPanel.error("job build fail !", e);
				}
			}
		};
	}

	private ActionListener createStopEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				if (ActionPanel.this.currentJob == null) {
					return;
				}

				try {
					ActionPanel.this.currentJob.terminate();
				} catch (Throwable e) {
					ActionPanel.this.logPanel.error("job terminate fail !", e);
				}
			}
		};
	}

	public void onStart(long startTime) {
		this.elapsedTimeLabel.setText("0:0:0");
		this.progressBar.setValue(0);
		this.progressLabel.setText("0.00 %");
		this.useMultiThreadCheckBox.setEnabled(false);
		this.maxThreadCountField.setEnabled(false);
		ActionPanel.this.startButton.setEnabled(false);
		ActionPanel.this.stopButton.setEnabled(true);
	}

	public void onDone(long startTime, long elapsedTime) {
		this.currentJob = null;
		this.elapsedTimeLabel.setText(this.formatElapsedTime(elapsedTime));
		this.progressBar.setValue(100);
		this.progressLabel.setText("100.00 %");
		this.useMultiThreadCheckBox.setEnabled(true);
		this.maxThreadCountField.setEnabled(true);
		ActionPanel.this.startButton.setEnabled(true);
		ActionPanel.this.stopButton.setEnabled(false);
	}

	public void onProgress(float progressRate, long startTime, long currentTime) {
		this.elapsedTimeLabel.setText(this.formatElapsedTime(currentTime - startTime));

		// 끝나기 전까지는 99%로 보여준다.
		float validRate = progressRate >= 100 ? 99 : progressRate;
		this.progressBar.setValue((int) validRate);
		this.progressLabel.setText(String.format("%.2f", validRate) + " %");
	}

	private String formatElapsedTime(long elapsedTime) {
		long elapsedSec = elapsedTime / 1000;
		long s = elapsedSec % 60;
		elapsedSec /= 60;
		long m = elapsedSec % 60;
		long h = elapsedSec / 60;
		return h + ":" + m + ":" + s;
	}

	public ActionData getActionData() {
		ActionData actionData = new ActionData();
		actionData.setUseMultiThread(this.useMultiThreadCheckBox.isSelected());
		actionData.setMaxThreadCount(Integer.parseInt(this.maxThreadCountField.getText()));
		return actionData;
	}

	@Override
	public void loadConfig() {
		ActionData actionData = new Gson().fromJson(this.appConfig.getUnsplitString(tabName + ".action.data"), ActionData.class);
		if (actionData == null) {
			return;
		}
		this.useMultiThreadCheckBox.setSelected(actionData.isUseMultiThread());
		this.maxThreadCountField.setText(actionData.getMaxThreadCount() + "");
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty(tabName + ".action.data", new Gson().toJson(this.getActionData()));
	}
}
