package com.inter6.mail.job.smtp;

import com.inter6.mail.gui.TabComponent;
import com.inter6.mail.gui.action.ActionPanel;
import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.data.EnvelopePanel;
import com.inter6.mail.gui.setting.ServerPanel;
import com.inter6.mail.job.thread.ThreadSupportJob;
import com.inter6.mail.model.action.ActionData;
import com.inter6.mail.model.data.EnvelopeData;
import com.inter6.mail.model.setting.ServerData;
import com.inter6.mail.module.TabComponentManager;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractSmtpSendJob implements TabComponent, ThreadSupportJob {

	protected String tabName;

	protected TabComponentManager tabComponentManager = TabComponentManager.getInstance();

	private StopWatch stopWatch;

	public AbstractSmtpSendJob(String tabName) {
		this.tabName = tabName;
	}

	@Override
	public void run() {
		this.execute();
	}

	@Override
	public void execute() {
		ActionPanel actionPanel = this.tabComponentManager.getTabComponent(tabName, ActionPanel.class);
		LogPanel logPanel = this.tabComponentManager.getTabComponent(tabName, LogPanel.class);
		try {
			this.stopWatch = new StopWatch();
			this.stopWatch.start();
			actionPanel.onStart(this.stopWatch.getStartTime());

			this.doSend();
			logPanel.info("smtp send done - JOB:" + this);
		} catch (Throwable e) {
			logPanel.error("smtp send fail ! - JOB:" + this, e);
		} finally {
			this.stopWatch.stop();
			actionPanel.onDone(this.stopWatch.getStartTime(), this.stopWatch.getTime());
		}
	}

	protected abstract void doSend() throws Throwable;

	public abstract void terminate() throws InterruptedException;

	protected ServerData getServerData() {
		return this.tabComponentManager.getTabComponent(tabName, ServerPanel.class).getServerData();
	}

	protected EnvelopeData getEnvelopeData() {
		return this.tabComponentManager.getTabComponent(tabName, EnvelopePanel.class).getEnvelopeData();
	}

	protected ActionData getActionData() {
		return this.tabComponentManager.getTabComponent(tabName, ActionPanel.class).getActionData();
	}

	protected long getStartTime() {
		return this.stopWatch.getStartTime();
	}
}
