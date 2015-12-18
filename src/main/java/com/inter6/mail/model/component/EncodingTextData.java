package com.inter6.mail.model.component;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;

@Data
public class EncodingTextData {
	private boolean isUse;
	private String text;
	private String charset;
	private String encoding;

	public String encodeSubject() throws UnsupportedEncodingException {
		return MimeUtility.encodeWord(StringUtils.defaultString(this.text, ""), StringUtils.defaultString(this.charset, "UTF-8"), StringUtils.defaultString(this.encoding, "B"));
	}
}
