package com.inter6.mail.job.smtp;

import com.inter6.mail.gui.action.ActionPanel;
import com.inter6.mail.job.thread.ThreadSupportJob;
import com.inter6.mail.model.action.ActionData;
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
        } finally {
            if (this.actionData.isUseMultiThread()) {
                terminateWorkers();
            }
            monitor.terminate();
        }
    }

    protected abstract void doMasterJob() throws Throwable;

    protected void orderWorker(ThreadSupportJob workerJob) throws Throwable {
        if (this.actionData.isUseMultiThread()) {
            this.workers.execute(workerJob);
        } else {
            workerJob.execute();
        }
    }

    @Override
    public void terminate() throws InterruptedException {
        terminateWorkers();
    }

    private void terminateWorkers() throws InterruptedException {
        synchronized (this.workerLock) {
            if (this.workers == null || !this.workers.isRun()) {
                return;
            }
            this.workers.terminate();
            while (this.workers.isRun()) {
                Thread.sleep(1000);
            }
            workers = null;
        }
    }

    protected abstract float getProgressRate();

    private final class JobProgressMonitor extends Thread {

        private boolean isRun;

        @Override
        public void run() {
            this.isRun = true;
            while (this.isRun) {
                ActionPanel actionPanel = tabComponentManager.getTabComponent(tabName, ActionPanel.class);
                actionPanel.onProgress(AbstractSmtpSendMasterJob.this, AbstractSmtpSendMasterJob.this.getProgressRate(), AbstractSmtpSendMasterJob.this.getStartTime(), System.currentTimeMillis());
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
