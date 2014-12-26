package com.inter6.mail.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmtpService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public Set<String> send(String host, int port, String sender, Set<String> receivers, InputStream messageStream) {
		SMTPClient smtpClient = null;
		try {
			smtpClient = this.connect(host, port, sender);
			Set<String> failReceivers = this.processEnvelope(smtpClient, sender, receivers);
			this.writeData(smtpClient, messageStream);
			this.close(smtpClient);
			return failReceivers;
		} catch (Throwable e) {
			this.log.error("send fail ! - RECV:" + receivers, e);
			return receivers;
		} finally {
			if (smtpClient != null && smtpClient.isConnected()) {
				try {
					smtpClient.disconnect();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	private SMTPClient connect(String host, int port, String sender) throws SocketException, IOException {
		SMTPClient smtpClient = new SMTPClient("UTF-8");
		smtpClient.setConnectTimeout(10 * 1000);
		smtpClient.connect(host, port);
		this.debug(smtpClient);

		smtpClient.helo(sender);
		this.debug(smtpClient);
		return smtpClient;
	}

	private Set<String> processEnvelope(SMTPClient smtpClient, String sender, Set<String> receivers) throws IOException {
		smtpClient.setSender(sender);
		this.debug(smtpClient);

		Set<String> failReceivers = new HashSet<String>();
		for (String receiver : receivers) {
			if (!smtpClient.addRecipient(receiver)) {
				failReceivers.add(receiver);
			}
			this.debug(smtpClient);
		}
		return failReceivers;
	}

	private void writeData(SMTPClient smtpClient, InputStream messageStream) throws IOException {
		Writer smtpWriter = null;
		try {
			smtpWriter = smtpClient.sendMessageData();
			this.debug(smtpClient);
			if (!(smtpWriter instanceof Writer)) {
				throw new IOException("smtp writer fail !");
			}

			ByteArrayOutputStream bais = new ByteArrayOutputStream(); // no required close()
			IOUtils.copy(messageStream, bais);
			ByteArrayInputStream baos = new ByteArrayInputStream(bais.toByteArray()); // no required close()
			this.writeMessage(smtpWriter, baos);
			IOUtils.closeQuietly(smtpWriter); // do close() before pending

			smtpClient.completePendingCommand();
			this.debug(smtpClient);
		} finally {
			IOUtils.closeQuietly(smtpWriter);
		}
	}

	private void writeMessage(Writer smtpWriter, InputStream messageStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(messageStream, "UTF-8"));
		BufferedWriter bw = new BufferedWriter(smtpWriter);
		this.processHeader(br, bw);
		this.processBody(br, bw);
		bw.flush();
	}

	private void processHeader(BufferedReader br, BufferedWriter bw) throws IOException {
		String line = null;
		while ((line = br.readLine()) != null && !this.isBodyStart(line)) {
			this.writeLine(bw, line);
		}
		this.writeLine(bw, StringUtils.defaultString(line));
	}

	private void processBody(BufferedReader br, BufferedWriter bw) throws IOException {
		char[] readBuffer = new char[1024 * 16]; // 16KB
		int readSize = -1;
		while ((readSize = br.read(readBuffer)) != -1) {
			bw.write(readBuffer, 0, readSize);
			this.debug(readBuffer);
		}
	}

	private boolean isBodyStart(String line) {
		return StringUtils.equals(line, "");
	}

	private void writeLine(BufferedWriter bw, String line) throws IOException {
		bw.write(line);
		bw.newLine();
		this.debug(line + "\n");
	}

	private void close(SMTPClient smtpClient) throws IOException {
		if (!SMTPReply.isPositiveCompletion(smtpClient.getReplyCode())) {
			throw new IOException("server refused connection ! - REPLY:" + smtpClient.getReplyString());
		}
		smtpClient.quit();
		this.debug(smtpClient);
	}

	private void debug(Object object) {
		if (object instanceof SMTPClient) {
			this.log.debug(((SMTPClient) object).getReplyString());
		} else if (object instanceof char[]) {
			this.log.debug(new String((char[]) object));
		} else {
			this.log.debug(object.toString());
		}
	}
}
