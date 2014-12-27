package com.inter6.mail.job.send;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
		String sender = (String) this.getEnvelopeData().get("envelope.from");
		@SuppressWarnings("unchecked")
		Set<String> receivers = (Set<String>) this.getEnvelopeData().get("envelope.to");
		InputStream messageStream = (InputStream) this.getData().get("messageStream");

		try {
			Set<String> failReceivers = this.smtpService.send(host, Integer.parseInt(port), sender, receivers, messageStream);
			if (CollectionUtils.isNotEmpty(failReceivers)) {
				throw new IOException("send parted success. exist fail receivers - RECV:" + failReceivers);
			}
		} finally {
			IOUtils.closeQuietly(messageStream);
		}
	}
}
