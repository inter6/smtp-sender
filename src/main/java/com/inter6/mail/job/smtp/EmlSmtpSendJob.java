package com.inter6.mail.job.smtp;

import java.io.FileInputStream;
import java.util.Collection;

import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.model.data.EmlSourceData;
import com.inter6.mail.module.ModuleService;

/**
 * files : List<File>
 * @author inter6
 */
@Component
@Scope("prototype")
public class EmlSmtpSendJob extends AbstractSmtpSendMasterJob {

	@Autowired
	private LogPanel logPanel;

	@Setter
	private EmlSourceData emlSourceData; // NOPMD

	private float progressRate;

	@Override
	protected void doMasterJob() {
		Collection<String> files = this.emlSourceData.getFiles();

		int idx = 0;
		for (String file : files) {
			try {
				MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
				mimeSmtpSendJob.setMessageStream(new FileInputStream(file));
				this.orderWorker(mimeSmtpSendJob);
			} catch (Throwable e) {
				this.logPanel.error("eml send order fail ! - EML:" + file, e);
			}

			idx++;
			this.progressRate = (float) idx / (float) files.size() * 100f;
		}
	}

	@Override
	protected float getProgressRate() {
		return this.progressRate;
	}

	@Override
	public String toString() {
		String info = "EmlSmtpSendJob FILES:" + this.emlSourceData.getFiles();
		if (info.length() > 50) {
			info = StringUtils.substring(info, 0, 100) + "...";
		}
		return info;
	}
}
