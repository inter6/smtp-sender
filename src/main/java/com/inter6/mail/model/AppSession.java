package com.inter6.mail.model;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.module.AppConfig;

@Component
public class AppSession implements ConfigObserver {

	@Autowired
	private AppConfig appConfig;

	@Getter
	@Setter
	private String lastSelectSourceDir = "/";

	@Getter
	@Setter
	private String lastSelectAttachDir = "/";

	@Override
	public void loadConfig() {
		this.lastSelectSourceDir = this.appConfig.getString("lastSelectSourceDir");
		if (StringUtils.isEmpty(this.lastSelectSourceDir)) {
			this.lastSelectSourceDir = "/";
		}
		this.lastSelectAttachDir = this.appConfig.getString("lastSelectAttachDir");
		if (StringUtils.isEmpty(this.lastSelectAttachDir)) {
			this.lastSelectAttachDir = "/";
		}
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("lastSelectSourceDir", this.lastSelectSourceDir);
		this.appConfig.setProperty("lastSelectAttachDir", this.lastSelectAttachDir);
	}
}
