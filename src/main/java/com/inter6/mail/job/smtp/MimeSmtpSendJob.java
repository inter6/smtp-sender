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
import com.inter6.mail.model.advanced.PostSendSettingData;
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
		try {
			String host = this.getServerData().getHost();
			int port = this.getServerData().getPort();
			String connectType = this.getServerData().getConnectType();

			String id = this.getServerData().getId();
			String password = this.getServerData().getPassword();
			AuthOption authOption = this.getServerData().getAuthOption();

			String mailFrom = this.getEnvelopeData().getMailFrom();
			Set<String> rcptTos = this.getEnvelopeData().getRcptTos();

			InputStream convertStream = null;
			try {
				convertStream = this.convertMessageStream(this.messageStream);
				Set<String> failReceivers = SmtpService
						.createInstance(host, port, connectType)
						.setAuth(authOption.getMethod(), id, password)
						.setEnvelope(mailFrom, rcptTos)
						.send(convertStream);
				if (CollectionUtils.isNotEmpty(failReceivers)) {
					throw new IOException("send parted success. exist fail receivers - RECV:" + failReceivers);
				}
			} finally {
				IOUtils.closeQuietly(convertStream);
			}
		} finally {
			IOUtils.closeQuietly(this.messageStream);
		}
	}

	private InputStream convertMessageStream(InputStream messageStream) throws MessagingException, IOException {
		ByteArrayOutputStream copyStream = new ByteArrayOutputStream();
		if (this.isParseMimeCondition()) {
			AdvancedMimeMessage mimeMessage = new AdvancedMimeMessage(messageStream);
			this.replaceSubject(mimeMessage);
			mimeMessage.saveChanges();
			mimeMessage.writeTo(copyStream);
			copyStream.flush();
		} else {
			IOUtils.copy(messageStream, copyStream);
		}

		this.saveToEml(copyStream);
		return new ByteArrayInputStream(copyStream.toByteArray());
	}

	private boolean isParseMimeCondition() {
		SubjectData replaceSubjectData = this.getPreSendSettingData().getReplaceSubjectData();
		if (replaceSubjectData.isUse()) {
			return true;
		}
		return false;
	}

	private boolean replaceSubject(AdvancedMimeMessage mimeMessage) throws UnsupportedEncodingException, MessagingException {
		SubjectData replaceSubjectData = this.getPreSendSettingData().getReplaceSubjectData();
		if (!replaceSubjectData.isUse()) {
			return false;
		}
		mimeMessage.setSubject(replaceSubjectData.encodeSubject());
		return true;
	}

	private boolean saveToEml(ByteArrayOutputStream copyStream) throws FileNotFoundException, IOException {
		PostSendSettingData postSendSettingData = this.getPostSendSettingData();
		if (!postSendSettingData.isSaveEml()) {
			return false;
		}
		String saveDirPath = postSendSettingData.getSaveEmlDir();
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

	@Override
	public void terminate() throws InterruptedException {
		// unsupport
	}

	@Override
	public float getProgressRate() {
		// unsupport
		return 0;
	}
}
