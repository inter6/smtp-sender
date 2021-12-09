package com.inter6.mail.job;

import com.inter6.mail.job.smtp.AbstractSmtpSendJob;

public interface SendJobBuilder {

    AbstractSmtpSendJob buildSendJob() throws Throwable;
}
