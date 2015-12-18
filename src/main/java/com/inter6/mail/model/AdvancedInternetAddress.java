package com.inter6.mail.model;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

public class AdvancedInternetAddress extends InternetAddress {
	private static final long serialVersionUID = -1167890302580784155L;

	public AdvancedInternetAddress(String address, String personal, String charset, String encoding) throws UnsupportedEncodingException {
		this.address = address;
		this.setPersonal(personal, charset, encoding);
	}

	public void setPersonal(String name, String charset, String encoding) throws UnsupportedEncodingException {
		this.personal = name;
		if (name != null) {
			this.encodedPersonal = MimeUtility.encodeWord(name, charset, encoding);
		} else {
			this.encodedPersonal = null;
		}
	}
}
