package com.inter6.mail.model.advanced;

import lombok.Data;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.EncodingTextData;

@Data
public class PreSendSettingData {
	private EncodingTextData replaceSubjectData;
	private DateData dateData;
}
