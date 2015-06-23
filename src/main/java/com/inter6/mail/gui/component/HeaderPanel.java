package com.inter6.mail.gui.component;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.inter6.mail.model.component.HeaderData;

public class HeaderPanel extends JPanel {
	private static final long serialVersionUID = -4451634940694824064L;

	private final JCheckBox useCheckBox = new JCheckBox();
	private final JTextField keyField = new JTextField(10);
	private final JTextArea valueArea = new JTextArea(2, 50);

	public HeaderPanel(boolean isUse) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.useCheckBox.setSelected(isUse);
		this.add(this.useCheckBox);
		this.add(this.keyField);
		this.add(new JLabel(":"));
		this.add(this.valueArea);
	}

	public HeaderData getHeaderData() {
		HeaderData headerData = new HeaderData();
		headerData.setUse(this.useCheckBox.isSelected());
		headerData.setKey(this.keyField.getText());
		headerData.setValue(this.valueArea.getText());
		return headerData;
	}

	public void setHeaderData(HeaderData headerData) {
		this.useCheckBox.setSelected(headerData.isUse());
		this.keyField.setText(headerData.getKey());
		this.valueArea.setText(headerData.getValue());
	}
}
