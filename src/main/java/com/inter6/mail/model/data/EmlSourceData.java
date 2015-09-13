package com.inter6.mail.model.data;

import com.inter6.mail.model.component.DateData;
import lombok.Data;

import java.util.Collection;

@Data
public class EmlSourceData {
	private Collection<String> files; // NOPMD
	private boolean isRecursive; // NOPMD
	private DateData replaceDateData; // NOPMD
}
