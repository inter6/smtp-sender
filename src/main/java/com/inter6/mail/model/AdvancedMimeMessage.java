package com.inter6.mail.model;

import org.apache.commons.lang3.ArrayUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;

public class AdvancedMimeMessage extends MimeMessage {

    public AdvancedMimeMessage(InputStream is) throws MessagingException {
        super(null, is);
    }

    @Override
    protected void updateMessageID() throws MessagingException {
        if (ArrayUtils.isNotEmpty(this.headers.getHeader("Message-ID"))) {
            return;
        }
        super.updateMessageID();
    }
}
