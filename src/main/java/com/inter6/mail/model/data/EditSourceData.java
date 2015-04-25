package com.inter6.mail.model.data;

import lombok.Data;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.SubjectData;
import com.inter6.mail.model.data.edit.EditAddressData;
import com.inter6.mail.model.data.edit.EditHeaderData;
import com.inter6.mail.model.data.edit.EditMessageData;

@Data
public class EditSourceData {
	private SubjectData subjectData; // NOPMD
	private DateData dateData; // NOPMD
	private EditAddressData editAddressData; // NOPMD
	private EditHeaderData editHeaderData; // NOPMD
	private EditMessageData editMessageData; // NOPMD
}
