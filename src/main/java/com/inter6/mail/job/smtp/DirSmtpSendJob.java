package com.inter6.mail.job.smtp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.model.data.DirSourceData;
import com.inter6.mail.module.ModuleService;

/**
 * dirs : List<File>
 * recursive : boolean
 * @author inter6
 */
@Component
@Scope("prototype")
public class DirSmtpSendJob extends AbstractSmtpSendMasterJob {

	@Autowired
	private LogPanel logPanel;

	@Setter
	private DirSourceData dirSourceData; // NOPMD

	private float progressRate;

	@Override
	protected void doMasterJob() {
		List<File> allFiles = new ArrayList<File>();

		for (File dir : this.dirSourceData.getDirs()) {
			Collection<File> files = FileUtils.listFiles(dir, new String[] { "eml" }, this.dirSourceData.isRecursive());
			if (CollectionUtils.isEmpty(files)) {
				continue;
			}
			allFiles.addAll(files);
		}

		int idx = 0;
		for (File file : allFiles) {
			try {
				MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
				mimeSmtpSendJob.setMessageStream(new FileInputStream(file));
				this.orderWorker(mimeSmtpSendJob);
			} catch (Throwable e) {
				this.logPanel.error("eml send order fail ! - EML:" + file, e);
			}

			idx++;
			this.progressRate = (float) idx / (float) allFiles.size() * 100f;
		}
	}

	@Override
	protected float getProgressRate() {
		return this.progressRate;
	}
}
