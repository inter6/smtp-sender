package com.inter6.mail.gui.component;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.inter6.mail.model.ContentType;

public class TextPartPanel extends ContentPanel {
	private static final long serialVersionUID = -5641431122402910873L;

	public TextPartPanel(String subType, Integer nested) {
		super(subType, nested);
	}

	@Override
	protected void initLayout(JPanel wrapPanel) {
		// TODO Auto-generated method stub
		wrapPanel.setLayout(new BorderLayout());
		wrapPanel.add(new JLabel("Content-Type: text/plain"), BorderLayout.NORTH);
		wrapPanel.add(new JTextArea(5, 30), BorderLayout.CENTER);
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ChildWrapPanel> addedChildPanels) {
		// TODO Auto-generated method stub
		return null;
	}
}
