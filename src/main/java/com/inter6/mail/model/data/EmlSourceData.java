package com.inter6.mail.model.data;

import java.util.Collection;

import lombok.Data;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.SendDelayData;

@Data
public class EmlSourceData {
	private Collection<String> files;
	private boolean isRecursive;
	private DateData replaceDateData;
	private SendDelayData sendDelayData;
}
