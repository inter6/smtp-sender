package com.inter6.mail.gui.component;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SubjectPanel extends JPanel {
	private static final long serialVersionUID = -4355410559162938889L;

	private final JCheckBox useCheckBox = new JCheckBox();
	private final JTextField subjectField = new JTextField(30);
	private final JTextField subjectCharsetField = new JTextField("UTF-8", 10);
	private final JComboBox subjectEncodingOptionBox = new JComboBox(new String[] { "B", "Q" });

	public SubjectPanel(String label, boolean isUse) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.useCheckBox.setSelected(isUse);
		this.add(this.useCheckBox);
		this.add(new JLabel(label));
		this.add(this.subjectField);
		this.add(this.subjectCharsetField);

		this.subjectEncodingOptionBox.setSelectedIndex(0);
		this.add(this.subjectEncodingOptionBox);
	}

	public Map<String, Object> getData() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("use", this.useCheckBox.isSelected());
		data.put("text", this.subjectField.getText());
		data.put("charset", this.subjectCharsetField.getText());
		data.put("encoding", this.subjectEncodingOptionBox.getSelectedItem());
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.useCheckBox.setSelected((Boolean) data.get("use"));
		this.subjectField.setText((String) data.get("text"));
		this.subjectCharsetField.setText((String) data.get("charset"));
		this.subjectEncodingOptionBox.setSelectedItem(data.get("encoding"));
	}
}
