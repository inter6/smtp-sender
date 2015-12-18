package com.inter6.mail.model;

import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.ArrayUtils;

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
