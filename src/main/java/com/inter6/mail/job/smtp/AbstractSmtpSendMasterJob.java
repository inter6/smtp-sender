package com.inter6.mail.job.smtp;

import com.inter6.mail.job.thread.ThreadSupportJob;
import com.inter6.mail.module.Workers;

public abstract class AbstractSmtpSendMasterJob extends AbstractSmtpSendJob {

	private Workers workers;
	private final Object workerLock = new Object();

	@Override
	protected void doSend() throws Throwable {
		try {
			synchronized (this.workerLock) {
				this.workers = new Workers();
				this.workers.initailize(this.getClass().getName());
			}
			this.doMasterJob();
			while (this.workers.isRun()) {
				Thread.sleep(1000);
			}
		} finally {
			synchronized (this.workerLock) {
				if (this.workers != null) {
					this.workers.stop();
				}
			}
		}
	}

	protected abstract void doMasterJob();

	protected void orderWorker(ThreadSupportJob workerJob) {
		this.workers.execute(workerJob);
	}

	@Override
	public void terminate() throws InterruptedException {
		synchronized (this.workerLock) {
			if (this.workers == null || !this.workers.isRun()) {
				return;
			}
			this.workers.terminate();
		}
	}
}
