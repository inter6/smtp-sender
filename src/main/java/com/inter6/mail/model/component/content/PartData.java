package com.inter6.mail.model.component.content;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.inter6.mail.model.ContentType;

@Getter
@Setter
@ToString
public class PartData {
	private ContentType contentType; // NOPMD
	private Collection<PartData> childPartDatas; // NOPMD
}
