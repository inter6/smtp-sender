package com.inter6.mail.model;


public enum AuthOption {
	NONE("None"), PLAIN_PASSWORD("Plain Password");

	private String text;

	private AuthOption(String text) {
		this.text = text;
	}

	public static AuthOption[] allItems() {
		return new AuthOption[] { NONE, PLAIN_PASSWORD };
	}

	public static AuthOption parse(String value) {
		if (NONE.toString().equals(value)) {
			return NONE;
		} else if (PLAIN_PASSWORD.toString().equals(value)) {
			return PLAIN_PASSWORD;
		} else {
			return NONE;
		}
	}

	@Override
	public String toString() {
		return this.text;
	}
}