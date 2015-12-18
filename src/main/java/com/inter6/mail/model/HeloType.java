package com.inter6.mail.model;

public enum HeloType {
	NONE("NONE"), HELO("HELO"), EHLO("EHLO");

	private String text;

	HeloType(String text) {
		this.text = text;
	}

	public static HeloType[] allItems() {
		return new HeloType[]{NONE, HELO, EHLO};
	}

	@Override
	public String toString() {
		return this.text;
	}
}