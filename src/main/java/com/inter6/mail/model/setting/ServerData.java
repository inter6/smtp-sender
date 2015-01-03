package com.inter6.mail.model.setting;

import lombok.Data;

import org.apache.commons.lang3.math.NumberUtils;

import com.inter6.mail.model.AuthOption;

@Data
public class ServerData {
	private String host; // NOPMD
	private int port; // NOPMD
	private boolean isSsl; // NOPMD
	private String id; // NOPMD
	private String password; // NOPMD
	private AuthOption authOption; // NOPMD

	public void setPort(String port) {
		if (!NumberUtils.isDigits(port)) {
			return;
		}
		this.port = Integer.parseInt(port);
	}
}
