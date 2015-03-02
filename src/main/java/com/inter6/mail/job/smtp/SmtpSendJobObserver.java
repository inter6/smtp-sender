package com.inter6.mail.job.smtp;

public interface SmtpSendJobObserver {

	public void onStart(long startTime);

	public void onSuccess();

	public void onError(Throwable e);

	public void onDone(long startTime, long elapsedTime);

	public void onProgress(float progressRate);
}
