package com.inter6.mail.gui.component.content;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;

import com.inter6.mail.model.ContentType;

public class MultiPartPanel extends ContentPartPanel {
	private static final long serialVersionUID = -4555796509776825034L;

	protected MultiPartPanel(ContentType contentType, Integer nested) {
		super(contentType, nested);
	}

	@Override
	protected void initLayout() {
		this.wrapPanel.setLayout(new BorderLayout());
		this.wrapPanel.add(new JLabel("Content-Type: " + this.contentType), BorderLayout.NORTH);
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ContentPartPanel> addedChildPanels) {
		Vector<ContentType> childTypes = new Vector<ContentType>();
		childTypes.add(ContentType.MULTIPART_MIXED);
		childTypes.add(ContentType.MULTIPART_ALTERNATIVE);
		childTypes.add(ContentType.TEXT_PLAIN);
		childTypes.add(ContentType.TEXT_HTML);
		childTypes.add(ContentType.ATTACHMENT);
		return childTypes;
	}
}
