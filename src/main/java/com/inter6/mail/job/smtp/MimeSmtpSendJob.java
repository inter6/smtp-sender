package com.inter6.mail.job.smtp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import javax.mail.MessagingException;

import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.model.AdvancedMimeMessage;
import com.inter6.mail.model.AuthOption;
import com.inter6.mail.model.advanced.AdvancedData;
import com.inter6.mail.model.component.SubjectData;
import com.inter6.mail.service.SmtpService;

/**
 * messageStream : InputStream
 * @author inter6
 */
@Component
@Scope("prototype")
public class MimeSmtpSendJob extends AbstractSmtpSendJob {

	@Setter
	private InputStream messageStream; // NOPMD

	@Override
	protected void doSend() throws Throwable {
		String host = this.getServerData().getHost();
		int port = this.getServerData().getPort();
		boolean isSsl = this.getServerData().isSsl();

		String id = this.getServerData().getId();
		String password = this.getServerData().getPassword();
		AuthOption authOption = this.getServerData().getAuthOption();

		String mailFrom = this.getEnvelopeData().getMailFrom();
		Set<String> rcptTos = this.getEnvelopeData().getRcptTos();

		this.messageStream = this.getAdvancedMessageStream(this.messageStream, this.getAdvancedData());

		try {
			Set<String> failReceivers = SmtpService
					.createInstance(host, port, isSsl)
					.setAuth(authOption.getMethod(), id, password)
					.setEnvelope(mailFrom, rcptTos)
					.send(this.messageStream);
			if (CollectionUtils.isNotEmpty(failReceivers)) {
				throw new IOException("send parted success. exist fail receivers - RECV:" + failReceivers);
			}
		} finally {
			IOUtils.closeQuietly(this.messageStream);
		}
	}

	private InputStream getAdvancedMessageStream(InputStream messageStream, AdvancedData advancedData) throws MessagingException, IOException {
		// TODO mime을 건드리지 않는 조건일 경우 파싱하지 않게 개선해야 됨
		AdvancedMimeMessage mimeMessage = new AdvancedMimeMessage(messageStream);
		this.replaceSubject(advancedData.getReplaceSubjectData(), mimeMessage);
		mimeMessage.saveChanges();

		ByteArrayOutputStream copyStream = new ByteArrayOutputStream();
		mimeMessage.writeTo(copyStream);
		copyStream.flush();

		this.saveToEml(advancedData, copyStream);
		return new ByteArrayInputStream(copyStream.toByteArray());
	}

	private boolean replaceSubject(SubjectData replaceSubjectData, AdvancedMimeMessage mimeMessage) throws UnsupportedEncodingException, MessagingException {
		if (!replaceSubjectData.isUse()) {
			return false;
		}
		mimeMessage.setSubject(replaceSubjectData.encodeSubject());
		return true;
	}

	private boolean saveToEml(AdvancedData advancedData, ByteArrayOutputStream copyStream) throws FileNotFoundException, IOException {
		if (!advancedData.isSaveEml()) {
			return false;
		}
		String saveDirPath = advancedData.getSaveEmlDir();
		if (StringUtils.isBlank(saveDirPath)) {
			return false;
		}
		File saveDir = new File(saveDirPath);
		FileUtils.forceMkdir(saveDir);
		OutputStream saveOutput = new FileOutputStream(new File(saveDir, System.currentTimeMillis() + ".eml"));
		try {
			IOUtils.copy(new ByteArrayInputStream(copyStream.toByteArray()), saveOutput);
		} finally {
			IOUtils.closeQuietly(saveOutput);
		}
		return true;
	}
}
