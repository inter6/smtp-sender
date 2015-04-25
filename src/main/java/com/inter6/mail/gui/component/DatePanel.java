package com.inter6.mail.gui.component;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.inter6.mail.model.component.DateData;

public class DatePanel extends JPanel {
	private static final long serialVersionUID = 4260562530907366011L;

	private final JCheckBox useCheckBox = new JCheckBox();
	private final JTextField textField = new JTextField(20);
	private final JCheckBox nowCheckBox = new JCheckBox("Now");

	public DatePanel(String label, int columns, boolean isUse, boolean isNow) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.useCheckBox.setSelected(isUse);
		this.add(this.useCheckBox);
		this.add(new JLabel(label));

		this.textField.setEnabled(!isNow);
		this.textField.setColumns(columns);
		this.add(this.textField);

		this.nowCheckBox.setSelected(isNow);
		this.nowCheckBox.addActionListener(this.nowEvent);
		this.add(this.nowCheckBox);
	}

	private final ActionListener nowEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
			DatePanel.this.textField.setEnabled(!DatePanel.this.nowCheckBox.isSelected());
		}
	};

	public DateData getDateData() {
		DateData dateData = new DateData();
		dateData.setUse(this.useCheckBox.isSelected());
		dateData.setText(this.textField.getText());
		dateData.setNow(this.nowCheckBox.isSelected());
		return dateData;
	}

	public void setDateData(DateData dateData) {
		this.useCheckBox.setSelected(dateData.isUse());
		this.textField.setText(dateData.getText());
		this.nowCheckBox.setSelected(dateData.isNow());
	}
}
