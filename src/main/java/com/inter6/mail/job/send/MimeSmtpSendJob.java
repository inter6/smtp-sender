package com.inter6.mail.job.send;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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
		String receiver = (String) this.getEnvelopeData().get("envelope.to");
		InputStream messageStream = (InputStream) this.getData().get("messageStream");

		int portInt = Integer.parseInt(port);
		Set<String> receivers = new HashSet<String>();
		receivers.add(receiver);

		try {
			this.smtpService.send(host, portInt, sender, receivers, messageStream);
		} finally {
			IOUtils.closeQuietly(messageStream);
		}
	}
}
