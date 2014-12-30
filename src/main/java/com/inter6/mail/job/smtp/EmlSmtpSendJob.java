package com.inter6.mail.job.smtp;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.module.ModuleService;

/**
 * files : List<File>
 * @author inter6
 */
@Component
@Scope("prototype")
public class EmlSmtpSendJob extends AbstractSmtpSendJob {

	@Override
	protected void doSend() throws Throwable {
		@SuppressWarnings("unchecked")
		List<File> files = (List<File>) this.getData().get("files");
		for (File file : files) {
			MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
			mimeSmtpSendJob.getData().put("messageStream", new FileInputStream(file));
			mimeSmtpSendJob.execute();
		}
	}
}
