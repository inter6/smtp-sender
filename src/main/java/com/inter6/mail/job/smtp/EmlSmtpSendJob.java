package com.inter6.mail.job.smtp;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.model.data.EmlSourceData;
import com.inter6.mail.module.ModuleService;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * files : List<File>
 *
 * @author inter6
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmlSmtpSendJob extends AbstractSmtpSendMasterJob {

	@Autowired
	private LogPanel logPanel;

	@Setter
	private EmlSourceData emlSourceData; // NOPMD

	private float progressRate;

	@Override
	protected void doMasterJob() throws Throwable {
		List<File> emlFiles = new ArrayList<>();

		for (String path : this.emlSourceData.getFiles()) {
			File file = new File(path);
			if (!file.exists()) {
				this.logPanel.info("not found file or directory - PATH:" + file);
				continue;
			}

			if (file.isDirectory()) {
				Collection<File> childEmls = FileUtils.listFiles(file, new String[]{"eml"}, this.emlSourceData.isRecursive());
				if (CollectionUtils.isEmpty(childEmls)) {
					continue;
				}
				emlFiles.addAll(childEmls);
			} else {
				emlFiles.add(file);
			}
		}
		this.logPanel.info("eml file count - COUNT:" + emlFiles.size());

		for (int i = 0; i < emlFiles.size(); i++) {
			File emlFile = emlFiles.get(i);
			try {
				MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
				mimeSmtpSendJob.setMessageStream(new FileInputStream(emlFile));
				mimeSmtpSendJob.setReplaceDateData(emlSourceData.getReplaceDateData());
				this.orderWorker(mimeSmtpSendJob);
			} catch (Throwable e) {
				this.logPanel.error("eml send order fail ! - EML:" + emlFile, e);
			}
			this.progressRate = (float) (i + 1) / (float) emlFiles.size() * 100f;
		}
	}

	@Override
	protected float getProgressRate() {
		return this.progressRate;
	}

	@Override
	public String toString() {
		String info = "EmlSmtpSendJob PATH:" + this.emlSourceData.getFiles();
		if (info.length() > 50) {
			info = StringUtils.substring(info, 0, 100) + "...";
		}
		return info;
	}
}
