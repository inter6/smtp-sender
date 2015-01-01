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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.component.SubjectPanel;
import com.inter6.mail.module.AppConfig;

@Component
public class AdvancedPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 44439027876264289L;

	@Autowired
	private AppConfig appConfig;

	private final SubjectPanel subjectPanel = new SubjectPanel("Replace Subject", true);

	private final JCheckBox saveUseCheckBox = new JCheckBox();
	private final JTextField savePathField = new JTextField(20);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(new JLabel("Advanced Setting"), BorderLayout.NORTH);

		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
		{
			wrapPanel.add(this.subjectPanel);

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

	public Map<String, Object> getData() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("advanced.replace.subject", this.subjectPanel.getData().get("use"));
		data.put("advanced.replace.subject.text", this.subjectPanel.getData().get("text"));
		data.put("advanced.replace.subject.charset", this.subjectPanel.getData().get("charset"));
		data.put("advanced.replace.subject.encoding", this.subjectPanel.getData().get("encoding"));
		data.put("advanced.save.eml", this.saveUseCheckBox.isSelected());
		data.put("advanced.save.eml.dir", this.savePathField.getText());
		return data;
	}

	@Override
	public void loadConfig() {
		Map<String, Object> subjectData = new HashMap<String, Object>();
		subjectData.put("use", this.appConfig.getBoolean("advanced.replace.subject", false));
		subjectData.put("text", this.appConfig.getString("advanced.replace.subject.text", ""));
		subjectData.put("charset", this.appConfig.getString("advanced.replace.subject.charset", "UTF-8"));
		subjectData.put("encoding", this.appConfig.getString("advanced.replace.subject.encoding", "B"));
		this.subjectPanel.setData(subjectData);

		this.saveUseCheckBox.setSelected(this.appConfig.getBoolean("advanced.save.eml", false));
		this.savePathField.setText(this.appConfig.getString("advanced.save.eml.dir"));
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("advanced.replace.subject", this.subjectPanel.getData().get("use"));
		this.appConfig.setProperty("advanced.replace.subject.text", this.subjectPanel.getData().get("text"));
		this.appConfig.setProperty("advanced.replace.subject.charset", this.subjectPanel.getData().get("charset"));
		this.appConfig.setProperty("advanced.replace.subject.encoding", this.subjectPanel.getData().get("encoding"));
		this.appConfig.setProperty("advanced.save.eml", this.saveUseCheckBox.isSelected());
		this.appConfig.setProperty("advanced.save.eml.dir", this.savePathField.getText());
	}
}
