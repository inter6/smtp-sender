package com.inter6.mail.job.smtp;

import java.util.Map;

import com.inter6.mail.job.thread.ThreadSupportJob;
import com.inter6.mail.model.action.ActionData;
import com.inter6.mail.module.ModuleService;
import com.inter6.mail.module.Workers;

public abstract class AbstractSmtpSendMasterJob extends AbstractSmtpSendJob {

	private Workers workers;
	private final Object workerLock = new Object();
	private ActionData actionData;

	@Override
	protected void doSend() throws Throwable {
		this.actionData = this.getActionData();

		JobProgressMonitor monitor = new JobProgressMonitor();
		try {
			if (this.actionData.isUseMultiThread()) {
				synchronized (this.workerLock) {
					this.workers = new Workers();
					this.workers.setPoolSize(this.actionData.getMaxThreadCount() / 2, this.actionData.getMaxThreadCount());
					this.workers.initailize(this.getClass().getName());
				}
			}
			monitor.start();
			this.doMasterJob();
			if (this.actionData.isUseMultiThread()) {
				while (this.workers.isRun()) {
					Thread.sleep(1000);
				}
			}
		} finally {
			if (this.actionData.isUseMultiThread() && this.workers != null) {
				synchronized (this.workerLock) {
					this.workers.terminate();
				}
			}
			monitor.terminate();
		}
	}

	protected abstract void doMasterJob();

	protected void orderWorker(ThreadSupportJob workerJob) throws Throwable {
		if (this.actionData.isUseMultiThread()) {
			this.workers.execute(workerJob);
		} else {
			workerJob.execute();
		}
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
					observer.onProgress(AbstractSmtpSendMasterJob.this.getProgressRate(),
							AbstractSmtpSendMasterJob.this.getStartTime(),
							System.currentTimeMillis());
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
