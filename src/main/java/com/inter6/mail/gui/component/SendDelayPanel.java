package com.inter6.mail.gui.component;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.math.NumberUtils;

import com.inter6.mail.model.component.SendDelayData;

public class SendDelayPanel extends JPanel {

	private final JCheckBox useCheckBox = new JCheckBox();
	private final JTextField sendDelayField = new JTextField(10);

	public SendDelayPanel() {
		super(new FlowLayout(FlowLayout.LEFT));

		this.useCheckBox.setText("Send Delay (Force Single-Thread)");
		this.add(this.useCheckBox);
		this.add(this.sendDelayField);
		this.add(new JLabel("sec"));
	}

	public SendDelayData getSendDelayData() {
		SendDelayData sendDelayData = new SendDelayData();
		sendDelayData.setUse(this.useCheckBox.isSelected());
		sendDelayData.setDelaySecond(NumberUtils.toInt(this.sendDelayField.getText()));
		return sendDelayData;
	}

	public void setSendDelayData(SendDelayData sendDelayData) {
		if (sendDelayData == null) {
			return;
		}
		this.useCheckBox.setSelected(sendDelayData.isUse());
		this.sendDelayField.setText(String.valueOf(sendDelayData.getDelaySecond()));
	}
}
