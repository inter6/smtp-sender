package com.inter6.mail.model.component;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;

@Data
public class EncodingTextData {
	private boolean isUse; // NOPMD
	private String text; // NOPMD
	private String charset; // NOPMD
	private String encoding; // NOPMD

	public String encodeSubject() throws UnsupportedEncodingException {
		return MimeUtility.encodeWord(StringUtils.defaultString(this.text, ""),
				StringUtils.defaultString(this.charset, "UTF-8"),
				StringUtils.defaultString(this.encoding, "B"));
	}
}
