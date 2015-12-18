package com.inter6.mail.model;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient.AUTH_METHOD;

public enum AuthOption {
	NONE("NONE", null), PLAIN("PLAIN", AUTH_METHOD.PLAIN), LOGIN("LOGIN", AUTH_METHOD.LOGIN), CRAM_MD5("CRAM_MD5", AUTH_METHOD.CRAM_MD5), XOAUTH("XOAUTH", AUTH_METHOD.XOAUTH);

	private String text;
	private AUTH_METHOD method;

	AuthOption(String text, AUTH_METHOD method) {
		this.text = text;
		this.method = method;
	}

	public static AuthOption[] allItems() {
		return new AuthOption[] { NONE, PLAIN, LOGIN, CRAM_MD5, XOAUTH };
	}

	@Override
	public String toString() {
		return this.text;
	}

	public AUTH_METHOD getMethod() {
		return this.method;
	}
}