package com.inter6.mail.gui.advanced;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.model.AuthOption;
import com.inter6.mail.model.EncodingOption;
import com.inter6.mail.module.AppConfig;

@Component
public class AdvancedPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 44439027876264289L;

	@Autowired
	private AppConfig appConfig;

	private final JCheckBox subjectUseCheckBox = new JCheckBox();
	private final JTextField subjectField = new JTextField(20);
	private final JTextField subjectCharsetField = new JTextField("UTF-8", 10);
	private final JComboBox subjectEncodingOptionBox = new JComboBox(EncodingOption.allItems());

	private final JCheckBox saveUseCheckBox = new JCheckBox();
	private final JTextField savePathField = new JTextField(20);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(new JLabel("Advanced Setting"), BorderLayout.NORTH);

		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
		{
			JPanel subjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				subjectPanel.add(this.subjectUseCheckBox);
				subjectPanel.add(new JLabel("Replace Subject"));
				subjectPanel.add(this.subjectField);
				subjectPanel.add(this.subjectCharsetField);

				this.subjectEncodingOptionBox.setSelectedIndex(0);
				subjectPanel.add(this.subjectEncodingOptionBox);
			}
			wrapPanel.add(subjectPanel);

			JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				savePanel.add(this.saveUseCheckBox);
				savePanel.add(new JLabel("Save EML"));

				this.savePathField.setEditable(false);
				savePanel.add(this.savePathField);

				JButton setSaveDirButton = new JButton("Directory");
				setSaveDirButton.addActionListener(this.setSaveDirEvent);
				savePanel.add(setSaveDirButton);
			}
			wrapPanel.add(savePanel);
		}

		JScrollPane wrapScrollPane = new JScrollPane(wrapPanel);
		wrapScrollPane.setPreferredSize(new Dimension(400, 0));
		this.add(wrapScrollPane, BorderLayout.CENTER);
	}

	private final ActionListener setSaveDirEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(AdvancedPanel.this) == JFileChooser.APPROVE_OPTION) {
				AdvancedPanel.this.savePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	};

	public Map<String, Object> getData() throws Throwable {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("subject.replace", this.subjectUseCheckBox.isSelected());
		data.put("subject.replace.text", this.subjectField.getText());
		data.put("subject.replace.charset", this.subjectCharsetField.getText());
		data.put("subject.replace.encoding", this.subjectEncodingOptionBox.getSelectedItem());
		data.put("save.eml", this.saveUseCheckBox.isSelected());
		data.put("save.eml.dir", this.savePathField.getText());
		return data;
	}

	@Override
	public void loadConfig() {
		this.subjectUseCheckBox.setSelected(this.appConfig.getBoolean("subject.replace", false));
		this.subjectField.setText(this.appConfig.getString("subject.replace.text"));
		this.subjectCharsetField.setText(this.appConfig.getString("subject.replace.charset"));
		this.subjectEncodingOptionBox.setSelectedIndex(AuthOption.parse(this.appConfig.getString("subject.replace.encoding")).ordinal());
		this.saveUseCheckBox.setSelected(this.appConfig.getBoolean("save.eml", false));
		this.savePathField.setText(this.appConfig.getString("save.eml.dir"));
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("subject.replace", this.subjectUseCheckBox.isSelected());
		this.appConfig.setProperty("subject.replace.text", this.subjectField.getText());
		this.appConfig.setProperty("subject.replace.charset", this.subjectCharsetField.getText());
		this.appConfig.setProperty("subject.replace.encoding", this.subjectEncodingOptionBox.getSelectedItem().toString());
		this.appConfig.setProperty("save.eml", this.saveUseCheckBox.isSelected());
		this.appConfig.setProperty("save.eml.dir", this.savePathField.getText());
	}
}
