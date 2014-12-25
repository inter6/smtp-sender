package com.inter6.mail.job;

public class BlankJobEvent implements JobEvent {

	public static final JobEvent BLANK = new BlankJobEvent();

	@Override
	public void onJobSuccess() {
		// do nothing
	}

	@Override
	public void onJobFail() {
		// do nothing
	}

	@Override
	public void onJobDone() {
		// do nothing
	}
}
