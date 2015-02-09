package com.inter6.mail.model.component.content;

import java.util.Collection;

import lombok.Data;

@Data
public class MultiPartData extends PartData {
	private Collection<PartData> childPartDatas; // NOPMD
}
