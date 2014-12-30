package com.inter6.mail.job.smtp;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import com.inter6.mail.module.ModuleService;

/**
 * dirs : List<File>
 * recursive : boolean
 * @author inter6
 */
public class DirSmtpSendJob extends AbstractSmtpSendJob {

	@Override
	protected void doSend() throws Throwable {
		@SuppressWarnings("unchecked")
		List<File> dirs = (List<File>) this.getData().get("dirs");
		boolean recursive = (Boolean) this.getData().get("recursive");

		for (File dir : dirs) {
			Collection<File> files = FileUtils.listFiles(dir, new String[] { "eml" }, recursive);
			if (CollectionUtils.isEmpty(files)) {
				continue;
			}
			EmlSmtpSendJob emlSmtpSendJob = ModuleService.getBean(EmlSmtpSendJob.class);
			emlSmtpSendJob.getData().put("files", files);
		}
	}
}
