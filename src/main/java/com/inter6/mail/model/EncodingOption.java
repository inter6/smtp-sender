package com.inter6.mail.model;

public enum EncodingOption {
	B("B"), Q("Q");

	private String text;

	private EncodingOption(String text) {
		this.text = text;
	}

	public static EncodingOption[] allItems() {
		return new EncodingOption[] { B, Q };
	}

	public static EncodingOption parse(String value) {
		for (EncodingOption encodingOption : allItems()) {
			if (encodingOption.toString().equals(value)) {
				return encodingOption;
			}
		}
		return B;
	}

	@Override
	public String toString() {
		return this.text;
	}
}
