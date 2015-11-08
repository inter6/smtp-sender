package com.inter6.mail.model.component.content;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class TextPartData extends PartData {
	private String textCharset;
	private String contentTransferEncoding;
	private String text;
}
