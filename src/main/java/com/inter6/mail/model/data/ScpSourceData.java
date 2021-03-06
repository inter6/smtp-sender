package com.inter6.mail.model.data;

import java.util.Collection;

import lombok.Data;

import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.SendDelayData;

@Data
public class ScpSourceData {
	private String host;
	private int port;
	private String username;
	private String password;

	private Collection<String> paths;
	private DateData replaceDateData;
	private SendDelayData sendDelayData;
}
