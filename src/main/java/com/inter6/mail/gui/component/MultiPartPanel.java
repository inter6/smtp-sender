package com.inter6.mail.gui.component;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.inter6.mail.model.ContentType;

public class MultiPartPanel extends ContentPanel {
	private static final long serialVersionUID = -4555796509776825034L;

	public MultiPartPanel(String subType, Integer nested) {
		super(subType, nested);
	}

	@Override
	protected void initLayout(JPanel wrapPanel) {
		// TODO Auto-generated method stub
		wrapPanel.setLayout(new BorderLayout());
		wrapPanel.add(new JLabel("Content-Type: multipart/mixed"), BorderLayout.NORTH);
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ChildWrapPanel> addedChildPanels) {
		// TODO Auto-generated method stub
		Vector<ContentType> childTypes = new Vector<ContentType>();
		childTypes.add(ContentType.MULTIPART_MIXED);
		childTypes.add(ContentType.TEXT_PLAIN);
		return childTypes;
	}
}
