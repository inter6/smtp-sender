package com.inter6.mail.model;

import org.apache.commons.lang3.StringUtils;

import com.inter6.mail.gui.component.content.AttachmentPartPanel;
import com.inter6.mail.gui.component.content.ContentPartPanel;
import com.inter6.mail.gui.component.content.MultiPartPanel;
import com.inter6.mail.gui.component.content.TextPartPanel;

public enum ContentType {
	MULTIPART_MIXED("multipart", "mixed", MultiPartPanel.class),
	MULTIPART_ALTERNATIVE("multipart", "alternative", MultiPartPanel.class),
	TEXT_PLAIN("text", "plain", TextPartPanel.class),
	TEXT_HTML("text", "html", TextPartPanel.class),
	ATTACHMENT("attachment", null, AttachmentPartPanel.class);

	private String type;
	private String subType;
	private Class<? extends ContentPartPanel> panelClass;

	private ContentType(String type, String subType, Class<? extends ContentPartPanel> panelClass) {
		this.type = type;
		this.subType = subType;
		this.panelClass = panelClass;
	}

	public String getSubType() {
		return this.subType;
	}

	public Class<? extends ContentPartPanel> getPanelClass() {
		return this.panelClass;
	}

	@Override
	public String toString() {
		return this.type + (StringUtils.isNotBlank(this.subType) ? "/" + this.subType : "");
	}
}