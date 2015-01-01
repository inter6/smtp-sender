package com.inter6.mail.gui.component;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddressPanel extends JPanel {
	private static final long serialVersionUID = -2074315658132902201L;

	private final JCheckBox useCheckBox = new JCheckBox();
	private final JComboBox typeOptionBox = new JComboBox(new String[] { "From", "To", "Cc", "Bcc" });
	private final JTextField personalField = new JTextField(15);
	private final JTextField personalCharsetField = new JTextField("UTF-8", 10);
	private final JComboBox personalEncodingOptionBox = new JComboBox(new String[] { "B", "Q" });
	private final JTextField addressField = new JTextField(20);

	public AddressPanel(String type, boolean isUse) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.useCheckBox.setSelected(isUse);
		this.add(this.useCheckBox);

		this.typeOptionBox.setSelectedItem(type);
		this.add(this.typeOptionBox);

		this.add(this.personalField);
		this.add(this.addressField);
		this.add(this.personalCharsetField);
		this.add(this.personalEncodingOptionBox);
	}

	public Map<String, Object> getData() throws Throwable {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("use", this.useCheckBox.isSelected());
		data.put("type", this.typeOptionBox.getSelectedItem());
		data.put("personal.text", this.personalField.getText());
		data.put("personal.charset", this.personalCharsetField.getText());
		data.put("personal.encoding", this.personalEncodingOptionBox.getSelectedItem());
		data.put("address.text", this.addressField.getText());
		return data;
	}
}
