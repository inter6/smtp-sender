package com.inter6.mail.model.component.content;

import com.inter6.mail.model.ContentType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@Getter
@Setter
@ToString
public class PartData {
	private ContentType contentType;
	private Collection<PartData> childPartDatas;
}
