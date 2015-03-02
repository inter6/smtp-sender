package com.inter6.mail.job.smtp;

import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.advanced.PostSendSettingPanel;
import com.inter6.mail.gui.advanced.PreSendSettingPanel;
import com.inter6.mail.gui.data.EnvelopePanel;
import com.inter6.mail.gui.setting.ServerPanel;
import com.inter6.mail.job.thread.ThreadSupportJob;
import com.inter6.mail.model.advanced.PostSendSettingData;
import com.inter6.mail.model.advanced.PreSendSettingData;
import com.inter6.mail.model.data.EnvelopeData;
import com.inter6.mail.model.setting.ServerData;
import com.inter6.mail.module.ModuleService;

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

	private StopWatch stopWatch;

	/*@Autowired
	private JobStatistics jobStatistics;*/

	@Override
	public void run() {
		this.execute();
	}

	@Override
	public void execute() {
		Map<String, SmtpSendJobObserver> observers = ModuleService.getBeans(SmtpSendJobObserver.class);
		try {
			this.stopWatch = new StopWatch();
			this.stopWatch.start();
			for (SmtpSendJobObserver observer : observers.values()) {
				observer.onStart(this.stopWatch.getStartTime());
			}

			this.doSend();
			this.logPanel.info("smtp send done - JOB:" + this.getClass().getSimpleName());
			//			this.jobStatistics.addCount(this, "success");

			for (SmtpSendJobObserver observer : observers.values()) {
				observer.onSuccess();
			}
		} catch (Throwable e) {
			this.logPanel.error("smtp send fail ! - JOB:" + this.getClass().getSimpleName(), e);
			//			this.jobStatistics.addCount(this, "fail");

			for (SmtpSendJobObserver observer : observers.values()) {
				observer.onError(e);
			}
		} finally {
			this.stopWatch.stop();
			for (SmtpSendJobObserver observer : observers.values()) {
				observer.onDone(this.stopWatch.getStartTime(), this.stopWatch.getTime());
			}
		}
	}

	protected abstract void doSend() throws Throwable;

	public abstract void terminate() throws InterruptedException;

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
