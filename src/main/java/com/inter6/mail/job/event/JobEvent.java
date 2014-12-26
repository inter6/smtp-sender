package com.inter6.mail.job.event;

public interface JobEvent {

	public void onJobStart();

	public void onJobSuccess();

	public void onJobFail(Throwable e);

	public void onJobDone();
}
