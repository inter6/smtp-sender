package com.inter6.mail.service;

public enum AuthOption {
	NONE("None"), PLAIN_PASSWORD("Plain Password");

	private String text;

	private AuthOption(String text) {
		this.text = text;
	}

	public static AuthOption[] allItems() {
		return new AuthOption[] { NONE, PLAIN_PASSWORD };
	}

	@Override
	public String toString() {
		return this.text;
	}
}