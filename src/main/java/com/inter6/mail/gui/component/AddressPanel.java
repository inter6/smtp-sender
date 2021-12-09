package com.inter6.mail.gui.component;

import com.inter6.mail.model.component.AddressData;

import javax.swing.*;
import java.awt.*;

public class AddressPanel extends JPanel {
    private static final long serialVersionUID = -2074315658132902201L;

    private final JCheckBox useCheckBox = new JCheckBox();
    private final JComboBox<String> typeOptionBox = new JComboBox<>(new String[]{"From", "To", "Cc", "Bcc"});
    private final JTextField personalField = new JTextField(10);
    private final JTextField personalCharsetField = new JTextField("UTF-8", 6);
    private final JComboBox<String> personalEncodingOptionBox = new JComboBox<>(new String[]{"B", "Q"});
    private final JTextField addressField = new JTextField(15);

    public AddressPanel() {
        this("to", false);
    }

    public AddressPanel(String type, boolean isUse) {
        super(new FlowLayout(FlowLayout.LEFT));

        this.useCheckBox.setSelected(isUse);
        this.add(this.useCheckBox);

        this.typeOptionBox.setSelectedItem(type);
        this.add(this.typeOptionBox);

        this.add(this.personalField);
        this.add(new JLabel("<"));
        this.add(this.addressField);
        this.add(new JLabel(">"));
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
