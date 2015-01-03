package com.inter6.mail.model;

import com.inter6.mail.gui.component.ContentPanel;
import com.inter6.mail.gui.component.MultiPartPanel;
import com.inter6.mail.gui.component.TextPartPanel;

public enum ContentType {
	MULTIPART_MIXED("multipart", "mixed", MultiPartPanel.class),
	TEXT_PLAIN("text", "plain", TextPartPanel.class);

	private String type;
	private String subType;
	private Class<? extends ContentPanel> panelClass;

	private ContentType(String type, String subType, Class<? extends ContentPanel> panelClass) {
		this.type = type;
		this.subType = subType;
		this.panelClass = panelClass;
	}

	public ContentPanel createPanel(int nested) throws Exception {
		return this.panelClass.getConstructor(String.class, Integer.class).newInstance(this.subType, nested);
	}

	@Override
	public String toString() {
		return this.type + "/" + this.subType;
	}
}
