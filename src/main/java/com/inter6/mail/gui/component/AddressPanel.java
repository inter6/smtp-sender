package com.inter6.mail.gui.component;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.inter6.mail.model.component.AddressData;

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

	public AddressData getAddressData() {
		AddressData addressData = new AddressData();
		addressData.setUse(this.useCheckBox.isSelected());
		addressData.setType((String) this.typeOptionBox.getSelectedItem());
		addressData.setPersonal(this.personalField.getText());
		addressData.setPersonalCharset(this.personalCharsetField.getText());
		addressData.setPersonalEncoding((String) this.personalEncodingOptionBox.getSelectedItem());
		addressData.setAddress(this.addressField.getText());
		return addressData;
	}

	public void setAddressData(AddressData addressData) {
		this.useCheckBox.setSelected(addressData.isUse());
		this.typeOptionBox.setSelectedItem(addressData.getType());
		this.personalField.setText(addressData.getPersonal());
		this.personalCharsetField.setText(addressData.getPersonalCharset());
		this.personalEncodingOptionBox.setSelectedItem(addressData.getPersonalEncoding());
		this.addressField.setText(addressData.getAddress());
	}
}
