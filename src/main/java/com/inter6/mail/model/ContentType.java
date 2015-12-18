package com.inter6.mail.model;

import org.apache.commons.lang3.StringUtils;

import com.inter6.mail.gui.component.content.AttachmentPartPanel;
import com.inter6.mail.gui.component.content.ContentPartPanel;
import com.inter6.mail.gui.component.content.MultiPartPanel;
import com.inter6.mail.gui.component.content.TextPartPanel;
import com.inter6.mail.model.component.content.AttachmentPartData;
import com.inter6.mail.model.component.content.MultiPartData;
import com.inter6.mail.model.component.content.PartData;
import com.inter6.mail.model.component.content.TextPartData;

public enum ContentType {
	MULTIPART_MIXED("multipart", "mixed", MultiPartPanel.class, MultiPartData.class), MULTIPART_ALTERNATIVE("multipart", "alternative", MultiPartPanel.class, MultiPartData.class), MULTIPART_RELATED("multipart", "related", MultiPartPanel.class, MultiPartData.class), TEXT_PLAIN("text", "plain", TextPartPanel.class, TextPartData.class), TEXT_HTML("text", "html", TextPartPanel.class, TextPartData.class), ATTACHMENT("attachment", null, AttachmentPartPanel.class, AttachmentPartData.class);

	private String type;
	private String subType;
	private Class<? extends ContentPartPanel> panelClass;
	private Class<? extends PartData> dataClass;

	ContentType(String type, String subType, Class<? extends ContentPartPanel> panelClass, Class<? extends PartData> dataClass) {
		this.type = type;
		this.subType = subType;
		this.panelClass = panelClass;
		this.dataClass = dataClass;
	}

	public String getSubType() {
		return this.subType;
	}

	public Class<? extends ContentPartPanel> getPanelClass() {
		return this.panelClass;
	}

	public Class<? extends PartData> getDataClass() {
		return this.dataClass;
	}

	@Override
	public String toString() {
		return this.type + (StringUtils.isNotBlank(this.subType) ? "/" + this.subType : "");
	}
}
