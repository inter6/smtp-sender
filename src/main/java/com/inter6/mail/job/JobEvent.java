package com.inter6.mail.job;

public interface JobEvent {

	public void onJobSuccess();

	public void onJobFail();

	public void onJobDone();
}
