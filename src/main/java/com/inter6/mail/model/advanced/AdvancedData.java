package com.inter6.mail.model.advanced;

import com.inter6.mail.model.component.SubjectData;

import lombok.Data;

@Data
public class AdvancedData {
	private SubjectData replaceSubjectData; // NOPMD

	private boolean isSaveEml; // NOPMD
	private String saveEmlDir; // NOPMD
}
