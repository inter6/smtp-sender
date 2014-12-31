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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient.AUTH_METHOD;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmtpService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String host;
	private int port;
	private boolean isSsl;
	private String encoding = "UTF-8";

	private AUTH_METHOD authMethod;
	private String username;
	private String password;

	private String sender;
	private Set<String> receivers;

	private SmtpService() {
	}

	public static SmtpService createInstance(String host, int port, boolean isSsl) {
		SmtpService smtpService = new SmtpService();
		smtpService.setHost(host, port, isSsl);
		return smtpService;
	}

	public SmtpService setHost(String host, int port, boolean isSsl) {
		this.host = host;
		this.port = port;
		this.isSsl = isSsl;
		return this;
	}

	public SmtpService setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public SmtpService setAuth(AUTH_METHOD authMethod, String username, String password) {
		this.authMethod = authMethod;
		this.username = username;
		this.password = password;
		return this;
	}

	public SmtpService setEnvelope(String sender, Set<String> receivers) {
		this.sender = sender;
		this.receivers = receivers;
		return this;
	}

	public Set<String> send(InputStream messageStream) throws IOException {
		AuthenticatingSMTPClient smtpClient = null;
		try {
			smtpClient = this.connect();
			this.auth(smtpClient);

			Set<String> failReceivers = this.processEnvelope(smtpClient);
			if (this.receivers.size() == failReceivers.size()) {
				throw new IOException("not allowed all rcpt to !");
			}

			this.writeData(smtpClient, messageStream);
			return failReceivers;
		} catch (Throwable e) {
			throw new IOException("send fail ! - " + e.getMessage() + " - RECV:" + this.receivers, e);
		} finally {
			this.close(smtpClient);
		}
	}

	private AuthenticatingSMTPClient connect() throws SocketException, IOException {
		AuthenticatingSMTPClient smtpClient = new AuthenticatingSMTPClient("SSL", this.isSsl, this.encoding);
		smtpClient.setConnectTimeout(10 * 1000);
		smtpClient.connect(this.host, this.port);
		this.debug(smtpClient);

		smtpClient.helo(this.sender);
		this.debug(smtpClient);
		return smtpClient;
	}

	private void auth(AuthenticatingSMTPClient smtpClient) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		if (this.authMethod == null) {
			return;
		}
		boolean isSuccess = smtpClient.auth(this.authMethod, this.username, this.password);
		this.debug(smtpClient);
		if (!isSuccess) {
			throw new IOException("auth fail ! - ID:" + this.username);
		}
	}

	private Set<String> processEnvelope(SMTPClient smtpClient) throws IOException {
		smtpClient.setSender(this.sender);
		this.debug(smtpClient);

		Set<String> failReceivers = new HashSet<String>();
		for (String receiver : this.receivers) {
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

			if (!SMTPReply.isPositiveCompletion(smtpClient.getReplyCode())) {
				throw new IOException("server refused connection ! - REPLY:" + smtpClient.getReplyString());
			}
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

	private void close(SMTPClient smtpClient) {
		if (smtpClient == null || !smtpClient.isConnected()) {
			return;
		}
		try {
			smtpClient.quit();
			this.debug(smtpClient);
		} catch (IOException e) {
			// do nothing
		}
		try {
			smtpClient.disconnect();
		} catch (IOException e) {
			// do nothing
		}
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
