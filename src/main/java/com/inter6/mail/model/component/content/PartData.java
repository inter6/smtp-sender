package com.inter6.mail.model.component.content;

import java.util.Collection;

import lombok.Data;

import com.inter6.mail.model.ContentType;

@Data
// FIXME equals 관련한 findbugs 레포트 참고
public class PartData {
	private ContentType contentType; // NOPMD
	private Collection<PartData> childPartDatas; // NOPMD
}
