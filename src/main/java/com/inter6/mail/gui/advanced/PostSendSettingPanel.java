package com.inter6.mail.gui.advanced;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.model.advanced.PostSendSettingData;
import com.inter6.mail.module.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class PostSendSettingPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 583204462394235063L;

	@Autowired
	private AppConfig appConfig;

	private final JCheckBox saveUseCheckBox = new JCheckBox();
	private final JTextField savePathField = new JTextField(20);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new LineBorder(Color.BLUE));

		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			labelPanel.add(new JLabel("Post Send Setting"));
		}
		this.add(labelPanel);

		JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			savePanel.add(this.saveUseCheckBox);
			savePanel.add(new JLabel("Save EML"));

			this.savePathField.setEditable(false);
			savePanel.add(this.savePathField);

			JButton setSaveDirButton = new JButton("Directory");
			setSaveDirButton.addActionListener(this.createSetSaveDirEvent());
			savePanel.add(setSaveDirButton);
		}
		this.add(savePanel);
	}

	private ActionListener createSetSaveDirEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fileChooser.showOpenDialog(PostSendSettingPanel.this) == JFileChooser.APPROVE_OPTION) {
					PostSendSettingPanel.this.savePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		};
	}

	public PostSendSettingData getPostSendSettingData() {
		PostSendSettingData postSendSettingData = new PostSendSettingData();
		postSendSettingData.setSaveEml(false/*this.saveUseCheckBox.isSelected()*/);
		postSendSettingData.setSaveEmlDir(this.savePathField.getText());
		return postSendSettingData;
	}

	@Override
	public void loadConfig() {
		PostSendSettingData postSendSettingData = new Gson().fromJson(this.appConfig.getUnsplitString("postSendSetting.data"), PostSendSettingData.class);
		if (postSendSettingData == null) {
			return;
		}
		this.saveUseCheckBox.setSelected(postSendSettingData.isSaveEml());
		this.savePathField.setText(postSendSettingData.getSaveEmlDir());
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("postSendSetting.data", new Gson().toJson(this.getPostSendSettingData()));
	}
}
