package com.inter6.mail.model.data;

import com.inter6.mail.model.component.DateData;
import lombok.Data;

import java.util.Collection;

@Data
public class ScpSourceData {
	private String host;
	private int port;
	private String username;
	private String password;

	private Collection<String> paths;
	private DateData replaceDateData;
}
