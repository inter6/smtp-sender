package com.inter6.mail.job.smtp;

import com.inter6.mail.model.AdvancedMimeMessage;
import com.inter6.mail.model.AuthOption;
import com.inter6.mail.model.HeloType;
import com.inter6.mail.model.component.DateData;
import com.inter6.mail.service.SmtpService;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

/**
 * messageStream : InputStream
 *
 * @author inter6
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MimeSmtpSendJob extends AbstractSmtpSendJob {

    @Setter
    private InputStream messageStream;

    @Setter
    private DateData replaceDateData;

    @Override
    protected void doSend() throws Throwable {
        try {
            String host = this.getServerData().getHost();
            int port = this.getServerData().getPort();
            String connectType = this.getServerData().getConnectType();
            HeloType heloType = this.getServerData().getHeloType();
            String heloDomain = this.getServerData().getHeloDomain();

            String id = this.getServerData().getId();
            String password = this.getServerData().getPassword();
            AuthOption authOption = this.getServerData().getAuthOption();

            String mailFrom = this.getEnvelopeData().getMailFrom();
            Set<String> rcptTos = this.getEnvelopeData().getRcptTos();

            InputStream convertStream = null;
            try {
                convertStream = this.convertMessageStream(this.messageStream);
                Set<String> failReceivers = SmtpService.createInstance(host, port, connectType)
                        .setHelo(heloType, heloDomain)
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
            //			this.replaceSubject(mimeMessage);
            this.replaceDate(mimeMessage);
            mimeMessage.saveChanges();
            mimeMessage.writeTo(copyStream);
            copyStream.flush();
        } else {
            IOUtils.copy(messageStream, copyStream);
        }

        //		this.saveToEml(copyStream);
        return new ByteArrayInputStream(copyStream.toByteArray());
    }

    private boolean isParseMimeCondition() {
        return replaceDateData != null && replaceDateData.isUse();
    }

	/*private boolean replaceSubject(AdvancedMimeMessage mimeMessage) throws UnsupportedEncodingException, MessagingException {
		EncodingTextData replaceSubjectData = this.getPreSendSettingData().getReplaceSubjectData();
		if (!replaceSubjectData.isUse()) {
			return false;
		}
		mimeMessage.setSubject(replaceSubjectData.encodeSubject());
		return true;
	}*/

    private boolean replaceDate(AdvancedMimeMessage mimeMessage) throws MessagingException {
        if (replaceDateData == null || !replaceDateData.isUse()) {
            return false;
        }
        if (replaceDateData.isNow()) {
            mimeMessage.setSentDate(new Date());
        } else {
            mimeMessage.setHeader("Date", replaceDateData.getText());
        }
        return true;
    }

	/*private boolean saveToEml(ByteArrayOutputStream copyStream) throws IOException {
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
	}*/

    @Override
    public void terminate() {
        // unsupported
    }

    @Override
    public String toString() {
        return "MimeSmtpSendJob";
    }
}
