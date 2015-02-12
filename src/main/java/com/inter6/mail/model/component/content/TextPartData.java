package com.inter6.mail.model.component.content;

import lombok.Data;

@Data
public class TextPartData extends PartData {
	private String textCharset; // NOPMD
	private String contentTransferEncoding; // NOPMD
	private String text; // NOPMD
}
