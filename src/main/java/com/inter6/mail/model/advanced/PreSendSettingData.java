package com.inter6.mail.model.advanced;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.EncodingTextData;
import lombok.Data;

@Data
public class PreSendSettingData {
	private EncodingTextData replaceSubjectData;
	private DateData dateData;
}
