package com.inter6.mail.job.smtp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.advanced.PostSendSettingPanel;
import com.inter6.mail.gui.advanced.PreSendSettingPanel;
import com.inter6.mail.gui.data.EnvelopePanel;
import com.inter6.mail.gui.setting.ServerPanel;
import com.inter6.mail.job.thread.ThreadSupportJob;
import com.inter6.mail.model.JobStatistics;
import com.inter6.mail.model.advanced.PostSendSettingData;
import com.inter6.mail.model.advanced.PreSendSettingData;
import com.inter6.mail.model.data.EnvelopeData;
import com.inter6.mail.model.setting.ServerData;

@Component
@Scope("prototype")
public abstract class AbstractSmtpSendJob implements ThreadSupportJob {

	@Autowired
	private ServerPanel serverPanel;

	@Autowired
	private EnvelopePanel envelopePanel;

	@Autowired
	private PreSendSettingPanel preSendSettingPanel;

	@Autowired
	private PostSendSettingPanel postSendSettingPanel;

	@Autowired
	private LogPanel logPanel;

	@Autowired
	private JobStatistics jobStatistics;

	@Override
	public void run() {
		this.execute();
	}

	@Override
	public void execute() {
		try {
			this.doSend();
			this.logPanel.info("smtp send done - JOB:" + this.getClass().getSimpleName());
			this.jobStatistics.addCount(this, "success");
		} catch (Throwable e) {
			this.logPanel.error("smtp send fail ! - JOB:" + this.getClass().getSimpleName(), e);
			this.jobStatistics.addCount(this, "fail");
		}
	}

	protected abstract void doSend() throws Throwable;

	public abstract void terminate() throws InterruptedException;

	public abstract float getProgressRate();

	protected ServerData getServerData() {
		return this.serverPanel.getServerData();
	}

	protected EnvelopeData getEnvelopeData() {
		return this.envelopePanel.getEnvelopeData();
	}

	protected PreSendSettingData getPreSendSettingData() {
		return this.preSendSettingPanel.getPreSendSettingData();
	}

	protected PostSendSettingData getPostSendSettingData() {
		return this.postSendSettingPanel.getPostSendSettingData();
	}
}
