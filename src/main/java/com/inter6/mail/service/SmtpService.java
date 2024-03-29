package com.inter6.mail.service;

import com.inter6.mail.model.HeloType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient.AUTH_METHOD;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

public class SmtpService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String host;
    private int port;
    private String connectType;
    private String encoding = "UTF-8";
    private HeloType heloType;
    private String heloDomain;

    private AUTH_METHOD authMethod;
    private String username;
    private String password;

    private String mailFrom;
    private Set<String> rcptTos;

    private SmtpService() {
    }

    public static SmtpService createInstance(String host, int port, String connectType) {
        return new SmtpService().setHost(host, port, connectType);
    }

    public SmtpService setHost(String host, int port, String connectType) {
        this.host = host;
        this.port = port;
        this.connectType = connectType;
        return this;
    }

    public SmtpService setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public SmtpService setHelo(HeloType heloType, String heloDomain) {
        this.heloType = heloType;
        this.heloDomain = heloDomain;
        return this;
    }

    public SmtpService setAuth(AUTH_METHOD authMethod, String username, String password) {
        this.authMethod = authMethod;
        this.username = username;
        this.password = password;
        return this;
    }

    public SmtpService setEnvelope(String mailFrom, Set<String> rcptTos) {
        this.mailFrom = mailFrom;
        this.rcptTos = rcptTos;
        return this;
    }

    public Set<String> send(InputStream messageStream) throws IOException {
        AuthenticatingSMTPClient smtpClient = null;
        try {
            smtpClient = this.connect();
            this.auth(smtpClient);

            Set<String> failReceivers = this.processEnvelope(smtpClient);
            if (this.rcptTos.size() == failReceivers.size()) {
                throw new IOException("not allowed all rcpt to !");
            }

            this.writeData(smtpClient, messageStream);
            return failReceivers;
        } catch (Throwable e) {
            throw new IOException("send fail ! - " + e.getMessage() + " - RECV:" + this.rcptTos, e);
        } finally {
            this.close(smtpClient);
        }
    }

    private AuthenticatingSMTPClient connect() throws IOException {
        AuthenticatingSMTPClient smtpClient;
        if ("ssl".equalsIgnoreCase(this.connectType)) {
            smtpClient = new AuthenticatingSMTPClient("SSL", true, this.encoding);
        } else if ("tls".equalsIgnoreCase(this.connectType)) {
            smtpClient = new AuthenticatingSMTPClient("TLS", false, this.encoding);
        } else {
            smtpClient = new AuthenticatingSMTPClient("SSL", false, this.encoding);
        }

        smtpClient.setConnectTimeout(10 * 1000);
        smtpClient.connect(this.host, this.port);
        this.debug(smtpClient);

        if (heloType != null) {
            switch (heloType) {
                case HELO:
                    smtpClient.helo(heloDomain);
                    this.debug(smtpClient);
                    break;
                case EHLO:
                    smtpClient.ehlo(heloDomain);
                    this.debug(smtpClient);
                    break;
                case NONE:
                default:
                    // do nothing
            }
        }

        if ("tls".equalsIgnoreCase(this.connectType)) {
            smtpClient.execTLS();
            this.debug(smtpClient);
        }

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
        smtpClient.setSender(this.mailFrom);
        this.debug(smtpClient);

        Set<String> failReceivers = new HashSet<>();
        for (String receiver : this.rcptTos) {
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
            if (smtpWriter == null) {
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
        BufferedReader br = new BufferedReader(new InputStreamReader(messageStream, StandardCharsets.UTF_8));
        BufferedWriter bw = new BufferedWriter(smtpWriter);
        this.processHeader(br, bw);
        this.processBody(br, bw);
        bw.flush();
    }

    private void processHeader(BufferedReader br, BufferedWriter bw) throws IOException {
        String line;
        while ((line = br.readLine()) != null && !this.isBodyStart(line)) {
            this.writeLine(bw, line);
        }
        this.writeLine(bw, StringUtils.defaultString(line));
    }

    private void processBody(BufferedReader br, BufferedWriter bw) throws IOException {
        char[] readBuffer = new char[1024 * 16]; // 16KB
        int readSize;
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
            log.debug("smtp close fail !", e);
        }
        try {
            smtpClient.disconnect();
        } catch (IOException e) {
            log.debug("smtp disconnect fail !", e);
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
