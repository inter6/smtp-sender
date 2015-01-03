package com.inter6.mail.job.smtp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.advanced.AdvancedPanel;
import com.inter6.mail.gui.data.EnvelopePanel;
import com.inter6.mail.gui.setting.ServerPanel;
import com.inter6.mail.job.Job;
import com.inter6.mail.model.JobStatistics;
import com.inter6.mail.model.advanced.AdvancedData;
import com.inter6.mail.model.data.EnvelopeData;
import com.inter6.mail.model.setting.ServerData;

@Component
@Scope("prototype")
public abstract class AbstractSmtpSendJob implements Job {

	@Autowired
	private ServerPanel serverPanel;

	@Autowired
	private EnvelopePanel envelopePanel;

	@Autowired
	private AdvancedPanel advancedPanel;

	@Autowired
	private JobStatistics jobStatistics;

	@Override
	public void execute() throws Throwable {
		try {
			this.doSend();
			this.jobStatistics.addCount(this, "success");
		} catch (Throwable e) {
			this.jobStatistics.addCount(this, "fail");
			throw e;
		}
	}

	protected abstract void doSend() throws Throwable;

	protected ServerData getServerData() {
		return this.serverPanel.getServerData();
	}

	protected EnvelopeData getEnvelopeData() {
		return this.envelopePanel.getEnvelopeData();
	}

	protected AdvancedData getAdvancedData() {
		return this.advancedPanel.getAdvancedData();
	}
}
