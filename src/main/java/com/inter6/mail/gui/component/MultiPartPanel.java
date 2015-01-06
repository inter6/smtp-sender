package com.inter6.mail.gui.component;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;

import com.inter6.mail.model.ContentType;

public class MultiPartPanel extends ContentPanel {
	private static final long serialVersionUID = -4555796509776825034L;

	protected MultiPartPanel(String subType, Integer nested) {
		super(subType, nested);
	}

	@Override
	protected void initLayout() {
		this.wrapPanel.setLayout(new BorderLayout());
		this.wrapPanel.add(new JLabel("Content-Type: multipart/" + this.subType), BorderLayout.NORTH);
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ChildWrapPanel> addedChildPanels) {
		Vector<ContentType> childTypes = new Vector<ContentType>();
		childTypes.add(ContentType.MULTIPART_MIXED);
		childTypes.add(ContentType.MULTIPART_ALTERNATIVE);
		childTypes.add(ContentType.TEXT_PLAIN);
		childTypes.add(ContentType.TEXT_HTML);
		childTypes.add(ContentType.ATTACHMENT);
		return childTypes;
	}
}
