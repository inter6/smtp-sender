package com.inter6.mail.gui.advanced;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.component.SubjectPanel;
import com.inter6.mail.model.advanced.AdvancedData;
import com.inter6.mail.model.component.SubjectData;
import com.inter6.mail.module.AppConfig;

@Component
public class AdvancedPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 44439027876264289L;

	@Autowired
	private AppConfig appConfig;

	private final SubjectPanel subjectPanel = new SubjectPanel("Replace Subject", false);

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

	public AdvancedData getAdvancedData() {
		AdvancedData advancedData = new AdvancedData();
		advancedData.setReplaceSubjectData(this.subjectPanel.getSubjectData());
		advancedData.setSaveEml(this.saveUseCheckBox.isSelected());
		advancedData.setSaveEmlDir(this.savePathField.getText());
		return advancedData;
	}

	@Override
	public void loadConfig() {
		AdvancedData advancedData = new Gson().fromJson(this.appConfig.getUnsplitString("advanced.data"), AdvancedData.class);
		if (advancedData == null) {
			return;
		}
		SubjectData replaceSubjectData = advancedData.getReplaceSubjectData();
		if (replaceSubjectData != null) {
			this.subjectPanel.setSubjectData(replaceSubjectData);
		}
		this.saveUseCheckBox.setSelected(advancedData.isSaveEml());
		this.savePathField.setText(advancedData.getSaveEmlDir());
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("advanced.data", new Gson().toJson(this.getAdvancedData()));
	}
}
