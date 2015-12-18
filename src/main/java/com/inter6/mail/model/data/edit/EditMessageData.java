package com.inter6.mail.model.data.edit;

import lombok.Data;

import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.component.content.PartData;

@Data
public class EditMessageData {
	private ContentType rootContentType;
	private PartData rootPartData;
}
