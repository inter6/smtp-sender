package com.inter6.mail.model.setting;

import lombok.Data;

import org.apache.commons.lang3.math.NumberUtils;

import com.inter6.mail.model.AuthOption;
import com.inter6.mail.model.HeloType;

@Data
public class ServerData {
	private String host;
	private int port;
	private String connectType;
	private String id;
	private String password;
	private AuthOption authOption;
	private HeloType heloType;
	private String heloDomain;

	public void setPort(String port) {
		if (!NumberUtils.isDigits(port)) {
			return;
		}
		this.port = Integer.parseInt(port);
	}
}
