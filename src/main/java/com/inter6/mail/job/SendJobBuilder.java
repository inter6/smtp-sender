package com.inter6.mail.job;

import com.inter6.mail.job.smtp.AbstractSmtpSendJob;


public interface SendJobBuilder {

	public AbstractSmtpSendJob buildSendJob() throws Throwable;
}
