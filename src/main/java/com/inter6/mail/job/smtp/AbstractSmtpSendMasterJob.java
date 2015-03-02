package com.inter6.mail.job.smtp;

import java.util.Map;

import com.inter6.mail.job.thread.ThreadSupportJob;
import com.inter6.mail.module.ModuleService;
import com.inter6.mail.module.Workers;

public abstract class AbstractSmtpSendMasterJob extends AbstractSmtpSendJob {

	private Workers workers;
	private final Object workerLock = new Object();

	@Override
	protected void doSend() throws Throwable {
		JobProgressMonitor monitor = new JobProgressMonitor();
		try {
			synchronized (this.workerLock) {
				this.workers = new Workers();
				this.workers.setPoolSize(4, 8);
				this.workers.initailize(this.getClass().getName());
			}
			monitor.start();
			this.doMasterJob();
			while (this.workers.isRun()) {
				Thread.sleep(1000);
			}
		} finally {
			synchronized (this.workerLock) {
				if (this.workers != null) {
					this.workers.terminate();
				}
			}
			monitor.terminate();
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

	protected abstract float getProgressRate();

	private final class JobProgressMonitor extends Thread {

		private boolean isRun;

		@Override
		public void run() {
			this.isRun = true;
			while (this.isRun) {
				Map<String, SmtpSendJobObserver> observers = ModuleService.getBeans(SmtpSendJobObserver.class);
				for (SmtpSendJobObserver observer : observers.values()) {
					observer.onProgress(AbstractSmtpSendMasterJob.this.getProgressRate());
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}

		public void terminate() {
			this.isRun = false;
			while (this.isAlive()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}
}
