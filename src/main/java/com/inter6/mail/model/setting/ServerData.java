package com.inter6.mail.model.setting;

import com.inter6.mail.model.AuthOption;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

@Data
public class ServerData {
	private String host;
	private int port;
	private String connectType;
	private String id;
	private String password;
	private AuthOption authOption;

	public void setPort(String port) {
		if (!NumberUtils.isDigits(port)) {
			return;
		}
		this.port = Integer.parseInt(port);
	}
}
