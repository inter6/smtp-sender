package com.inter6.mail.job.smtp;

import java.io.File;
import java.io.FileInputStream;

import lombok.Setter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.model.data.EmlSourceData;
import com.inter6.mail.module.ModuleService;

/**
 * files : List<File>
 * @author inter6
 */
@Component
@Scope("prototype")
public class EmlSmtpSendJob extends AbstractSmtpSendJob {

	@Setter
	private EmlSourceData emlSourceData; // NOPMD

	@Override
	protected void doSend() throws Throwable {
		for (File file : this.emlSourceData.getFiles()) {
			MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
			mimeSmtpSendJob.setMessageStream(new FileInputStream(file));
			mimeSmtpSendJob.execute();
		}
	}
}
