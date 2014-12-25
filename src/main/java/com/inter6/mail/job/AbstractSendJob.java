package com.inter6.mail.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public abstract class AbstractSendJob implements Job {

	protected Map<String, Object> data = new HashMap<String, Object>();
	protected JobEvent jobEvent = BlankJobEvent.BLANK;

	public Map<String, Object> getData() {
		return this.data;
	}

	public void setJobEvent(JobEvent jobEvent) {
		this.jobEvent = jobEvent;
	}

	@Override
	public void execute() {
		try {
			this.doSend();
			this.jobEvent.onJobSuccess();
		} catch (Throwable e) {
			this.jobEvent.onJobFail();
		}
		this.jobEvent.onJobDone();
	}

	protected abstract void doSend() throws Throwable;
}
