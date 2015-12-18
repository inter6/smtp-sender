package com.inter6.mail.gui.component;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.inter6.mail.model.component.EncodingTextData;

public class EncodingTextPanel extends JPanel {
	private static final long serialVersionUID = -4355410559162938889L;

	private final JCheckBox useCheckBox = new JCheckBox();
	private final JTextField textField = new JTextField(20);
	private final JTextField charsetField = new JTextField("UTF-8", 6);
	private final JComboBox<String> encodingOptionBox = new JComboBox<>(new String[] { "B", "Q" });

	public EncodingTextPanel(String label, int columns, boolean isUse) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.useCheckBox.setSelected(isUse);
		this.useCheckBox.setText(label);
		this.add(this.useCheckBox);
		this.textField.setColumns(columns);
		this.add(this.textField);
		this.add(this.charsetField);

		this.encodingOptionBox.setSelectedIndex(0);
		this.add(this.encodingOptionBox);
	}

	public EncodingTextData getEncodingTextData() {
		EncodingTextData encodingTextData = new EncodingTextData();
		encodingTextData.setUse(this.useCheckBox.isSelected());
		encodingTextData.setText(this.textField.getText());
		encodingTextData.setCharset(this.charsetField.getText());
		encodingTextData.setEncoding((String) this.encodingOptionBox.getSelectedItem());
		return encodingTextData;
	}

	public void setEncodingTextData(EncodingTextData encodingTextData) {
		this.useCheckBox.setSelected(encodingTextData.isUse());
		this.textField.setText(encodingTextData.getText());
		this.charsetField.setText(encodingTextData.getCharset());
		this.encodingOptionBox.setSelectedItem(encodingTextData.getEncoding());
	}

	public void setUse(boolean isUse) {
		this.useCheckBox.setSelected(isUse);
	}

	public void setText(String text) {
		textField.setText(text);
	}
}
