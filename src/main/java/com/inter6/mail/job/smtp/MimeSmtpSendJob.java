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
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.model.AdvancedMimeMessage;
import com.inter6.mail.model.AuthOption;
import com.inter6.mail.model.EncodingOption;
import com.inter6.mail.service.SmtpService;
import com.inter6.mail.util.ObjectUtil;

/**
 * messageStream : InputStream
 * @author inter6
 */
@Component
@Scope("prototype")
public class MimeSmtpSendJob extends AbstractSmtpSendJob {

	@Override
	protected void doSend() throws Throwable {
		String host = (String) this.getServerData().get("server.host");
		String port = (String) this.getServerData().get("server.port");
		boolean isSsl = (Boolean) this.getServerData().get("server.ssl");

		String id = (String) this.getServerData().get("user.id");
		String password = (String) this.getServerData().get("user.password");
		AuthOption authOption = (AuthOption) this.getServerData().get("server.authOption");

		String sender = (String) this.getEnvelopeData().get("envelope.from");
		@SuppressWarnings("unchecked")
		Set<String> receivers = (Set<String>) this.getEnvelopeData().get("envelope.to");
		InputStream messageStream = (InputStream) this.getData().get("messageStream");

		messageStream = this.getAdvancedMessageStream(messageStream, this.getAdvancedData());

		try {
			Set<String> failReceivers = SmtpService
					.createInstance(host, Integer.parseInt(port), isSsl)
					.setAuth(authOption.getMethod(), id, password)
					.setEnvelope(sender, receivers)
					.send(messageStream);
			if (CollectionUtils.isNotEmpty(failReceivers)) {
				throw new IOException("send parted success. exist fail receivers - RECV:" + failReceivers);
			}
		} finally {
			IOUtils.closeQuietly(messageStream);
		}
	}

	private InputStream getAdvancedMessageStream(InputStream messageStream, Map<String, Object> advancedData) throws MessagingException, IOException {
		if (MapUtils.isEmpty(advancedData)) {
			return messageStream;
		}

		// TODO mime을 건드리지 않는 조건일 경우 파싱하지 않게 개선해야 됨
		AdvancedMimeMessage mimeMessage = new AdvancedMimeMessage(messageStream);
		this.replaceSubject(advancedData, mimeMessage);
		mimeMessage.saveChanges();

		ByteArrayOutputStream copyStream = new ByteArrayOutputStream();
		mimeMessage.writeTo(copyStream);
		copyStream.flush();

		this.saveToEml(advancedData, copyStream);
		return new ByteArrayInputStream(copyStream.toByteArray());
	}

	private boolean replaceSubject(Map<String, Object> advancedData, AdvancedMimeMessage mimeMessage) throws UnsupportedEncodingException, MessagingException {
		if (!ObjectUtil.defaultBoolean(advancedData.get("subject.replace"), false)) {
			return false;
		}
		String subject = ObjectUtil.defaultString(advancedData.get("subject.replace.text"), "");
		String charset = ObjectUtil.defaultString(advancedData.get("subject.replace.charset"), "UTF-8");
		EncodingOption encoding = (EncodingOption) advancedData.get("subject.replace.encoding");
		String encodeSubject = MimeUtility.encodeWord(subject, charset, encoding.toString());
		mimeMessage.setSubject(encodeSubject);
		return true;
	}

	private boolean saveToEml(Map<String, Object> advancedData, ByteArrayOutputStream copyStream) throws FileNotFoundException, IOException {
		if (!ObjectUtil.defaultBoolean(advancedData.get("save.eml"), false)) {
			return false;
		}
		String saveDirPath = ObjectUtil.defaultString(advancedData.get("save.eml.dir"), "");
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
