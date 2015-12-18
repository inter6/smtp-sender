package com.inter6.mail.model.data.edit;

import java.util.Collection;

import lombok.Data;

import com.inter6.mail.model.component.HeaderData;

@Data
public class EditHeaderData {
	private Collection<HeaderData> headerDatas;
}
