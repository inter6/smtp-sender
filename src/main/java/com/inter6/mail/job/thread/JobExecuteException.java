package com.inter6.mail.job.thread;

public class JobExecuteException extends RuntimeException {
	private static final long serialVersionUID = -8197551587299842462L;

	public JobExecuteException(String message, Throwable cause) {
		super(message, cause);
	}
}
