package com.inter6.mail.job.smtp;

import java.io.File;
import java.util.Collection;

import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import com.inter6.mail.model.data.DirSourceData;
import com.inter6.mail.model.data.EmlSourceData;
import com.inter6.mail.module.ModuleService;

/**
 * dirs : List<File>
 * recursive : boolean
 * @author inter6
 */
public class DirSmtpSendJob extends AbstractSmtpSendJob {

	@Setter
	private DirSourceData dirSourceData; // NOPMD

	@Override
	protected void doSend() throws Throwable {
		for (File dir : this.dirSourceData.getDirs()) {
			Collection<File> files = FileUtils.listFiles(dir, new String[] { "eml" }, this.dirSourceData.isRecursive());
			if (CollectionUtils.isEmpty(files)) {
				continue;
			}

			EmlSmtpSendJob emlSmtpSendJob = ModuleService.getBean(EmlSmtpSendJob.class);
			emlSmtpSendJob.setEmlSourceData(new EmlSourceData(files));
			emlSmtpSendJob.execute();
		}
	}
}
