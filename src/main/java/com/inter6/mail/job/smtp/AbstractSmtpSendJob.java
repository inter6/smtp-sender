package com.inter6.mail.job.smtp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.advanced.AdvancedPanel;
import com.inter6.mail.gui.data.EnvelopePanel;
import com.inter6.mail.gui.setting.ServerPanel;
import com.inter6.mail.job.Job;
import com.inter6.mail.model.JobStatistics;

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

	private final Map<String, Object> data = new HashMap<String, Object>();

	public Map<String, Object> getData() {
		return this.data;
	}

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

	protected Map<String, Object> getAdvancedData() throws Throwable {
		return this.advancedPanel.getData();
	}
}
