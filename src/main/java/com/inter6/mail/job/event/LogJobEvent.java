package com.inter6.mail.job.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;

@Component
public class LogJobEvent implements JobEvent {

	@Autowired
	private LogPanel logPanel;

	@Override
	public void onJobStart() {
		this.logPanel.info("job start");
	}

	@Override
	public void onJobSuccess() {
		this.logPanel.info("job success");
	}

	@Override
	public void onJobFail(Throwable e) {
		this.logPanel.error("job fail ! -", e);
	}

	@Override
	public void onJobDone() {
		this.logPanel.info("job done");
	}
}
