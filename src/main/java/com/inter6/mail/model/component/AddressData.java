package com.inter6.mail.model.component;

import java.io.UnsupportedEncodingException;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;

import lombok.Data;

import com.inter6.mail.model.AdvancedInternetAddress;

@Data
public class AddressData {
	private boolean isUse; // NOPMD
	private String type; // NOPMD
	private String personal; // NOPMD
	private String personalCharset; // NOPMD
	private String personalEncoding; // NOPMD
	private String address; // NOPMD

	public RecipientType getRecipientType() {
		if ("To".equalsIgnoreCase(this.type)) {
			return RecipientType.TO;
		} else if ("Cc".equalsIgnoreCase(this.type)) {
			return RecipientType.CC;
		} else if ("Bcc".equalsIgnoreCase(this.type)) {
			return RecipientType.BCC;
		} else {
			return null;
		}
	}

	public InternetAddress toInternetAddress() throws UnsupportedEncodingException {
		return new AdvancedInternetAddress(this.address, this.personal, this.personalCharset, this.personalEncoding);
	}
}
