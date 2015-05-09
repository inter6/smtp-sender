package com.inter6.mail.model.advanced;

import lombok.Data;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.SubjectData;

@Data
public class PreSendSettingData {
	private SubjectData replaceSubjectData; // NOPMD
	private DateData dateData; // NOPMD
}
