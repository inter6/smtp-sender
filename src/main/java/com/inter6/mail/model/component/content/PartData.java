package com.inter6.mail.model.component.content;

import java.util.Collection;

import lombok.Data;

import com.inter6.mail.model.ContentType;

@Data
public class PartData {
	private ContentType contentType; // NOPMD
	private Collection<PartData> childPartDatas; // NOPMD
}
