package com.inter6.mail.job.send;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.data.EnvelopePanel;
import com.inter6.mail.gui.setting.ServerPanel;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.event.JobEvent;
import com.inter6.mail.job.event.LogJobEvent;
import com.inter6.mail.module.ModuleService;
import com.inter6.mail.service.SmtpService;

@Component
@Scope("prototype")
public abstract class AbstractSmtpSendJob implements Job {

	@Autowired
	private ServerPanel serverPanel;

	@Autowired
	private EnvelopePanel envelopePanel;

	@Autowired
	protected SmtpService smtpService;

	private final Map<String, Object> data = new HashMap<String, Object>();
	private JobEvent jobEvent = ModuleService.getBean(LogJobEvent.class);

	public Map<String, Object> getData() {
		return this.data;
	}

	public void setJobEvent(JobEvent jobEvent) {
		this.jobEvent = jobEvent;
	}

	@Override
	public void execute() {
		this.jobEvent.onJobStart();
		try {
			this.doSend();
			this.jobEvent.onJobSuccess();
		} catch (Throwable e) {
			this.jobEvent.onJobFail(e);
		}
		this.jobEvent.onJobDone();
	}

	protected abstract void doSend() throws Throwable;

	/**
	 * server.host : String
	 * server.port : String
	 * server.ssl : boolean
	 * user.id : String
	 * user.password : String
	 * server.authOption : AuthOption
	 * @return
	 * @throws Throwable
	 */
	protected Map<String, Object> getServerData() throws Throwable {
		return this.serverPanel.getData();
	}

	/**
	 * envelope.from : String
	 * envelope.to : Set<String>
	 * @return
	 * @throws Throwable
	 */
	protected Map<String, Object> getEnvelopeData() throws Throwable {
		return this.envelopePanel.getData();
	}
}
