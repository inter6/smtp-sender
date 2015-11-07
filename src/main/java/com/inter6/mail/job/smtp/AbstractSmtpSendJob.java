package com.inter6.mail.job.smtp;

import com.inter6.mail.gui.action.ActionPanel;
import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.data.EnvelopePanel;
import com.inter6.mail.gui.setting.ServerPanel;
import com.inter6.mail.job.thread.ThreadSupportJob;
import com.inter6.mail.model.action.ActionData;
import com.inter6.mail.model.data.EnvelopeData;
import com.inter6.mail.model.setting.ServerData;
import com.inter6.mail.module.ModuleService;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractSmtpSendJob implements ThreadSupportJob {

	@Autowired
	private ServerPanel serverPanel;

	@Autowired
	private EnvelopePanel envelopePanel;

	@Autowired
	private ActionPanel actionPanel;

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
		//		this.jobStatistics.clear();
		try {
			this.stopWatch = new StopWatch();
			this.stopWatch.start();
			for (SmtpSendJobObserver observer : observers.values()) {
				observer.onStart(this.stopWatch.getStartTime());
			}

			this.doSend();
			this.logPanel.info("smtp send done - JOB:" + this);
			//			this.jobStatistics.addCount(this, "success");
		} catch (Throwable e) {
			this.logPanel.error("smtp send fail ! - JOB:" + this, e);
			//			this.jobStatistics.addCount(this, "fail");
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

	protected ActionData getActionData() {
		return this.actionPanel.getActionData();
	}

	protected long getStartTime() {
		return this.stopWatch.getStartTime();
	}
}
