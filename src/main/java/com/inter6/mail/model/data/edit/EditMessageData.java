package com.inter6.mail.model.data.edit;

import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.component.content.PartData;
import lombok.Data;

@Data
public class EditMessageData {
	private ContentType rootContentType;
	private PartData rootPartData;
}
