package com.inter6.mail.gui.component;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.inter6.mail.model.component.SubjectData;

public class SubjectPanel extends JPanel {
	private static final long serialVersionUID = -4355410559162938889L;

	private final JCheckBox useCheckBox = new JCheckBox();
	private final JTextField subjectField = new JTextField(20);
	private final JTextField subjectCharsetField = new JTextField("UTF-8", 6);
	private final JComboBox subjectEncodingOptionBox = new JComboBox(new String[] { "B", "Q" });

	public SubjectPanel(String label, int columns, boolean isUse) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.useCheckBox.setSelected(isUse);
		this.add(this.useCheckBox);
		this.add(new JLabel(label));
		this.subjectField.setColumns(columns);
		this.add(this.subjectField);
		this.add(this.subjectCharsetField);

		this.subjectEncodingOptionBox.setSelectedIndex(0);
		this.add(this.subjectEncodingOptionBox);
	}

	public SubjectData getSubjectData() {
		SubjectData subjectData = new SubjectData();
		subjectData.setUse(this.useCheckBox.isSelected());
		subjectData.setText(this.subjectField.getText());
		subjectData.setCharset(this.subjectCharsetField.getText());
		subjectData.setEncoding((String) this.subjectEncodingOptionBox.getSelectedItem());
		return subjectData;
	}

	public void setSubjectData(SubjectData subjectData) {
		this.useCheckBox.setSelected(subjectData.isUse());
		this.subjectField.setText(subjectData.getText());
		this.subjectCharsetField.setText(subjectData.getCharset());
		this.subjectEncodingOptionBox.setSelectedItem(subjectData.getEncoding());
	}
}
