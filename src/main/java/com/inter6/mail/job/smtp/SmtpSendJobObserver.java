package com.inter6.mail.job.smtp;

public interface SmtpSendJobObserver {

	void onStart(long startTime);

	void onDone(long startTime, long elapsedTime);

	void onProgress(float progressRate, long startTime, long currentTime);
}
