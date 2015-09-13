package com.inter6.mail.model.data;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.EncodingTextData;
import com.inter6.mail.model.data.edit.EditAddressData;
import com.inter6.mail.model.data.edit.EditHeaderData;
import com.inter6.mail.model.data.edit.EditMessageData;
import lombok.Data;

@Data
public class EditSourceData {
	private EncodingTextData subjectData; // NOPMD
	private DateData dateData; // NOPMD
	private EditAddressData editAddressData; // NOPMD
	private EditHeaderData editHeaderData; // NOPMD
	private EditMessageData editMessageData; // NOPMD
}
