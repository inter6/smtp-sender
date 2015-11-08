package com.inter6.mail.model.component;

import com.inter6.mail.model.AdvancedInternetAddress;
import lombok.Data;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

@Data
public class AddressData {
	private boolean isUse;
	private String type;
	private String personal;
	private String personalCharset;
	private String personalEncoding;
	private String address;

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
