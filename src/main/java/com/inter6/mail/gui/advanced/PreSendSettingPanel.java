package com.inter6.mail.gui.advanced;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.component.SubjectPanel;
import com.inter6.mail.model.advanced.PreSendSettingData;
import com.inter6.mail.model.component.SubjectData;
import com.inter6.mail.module.AppConfig;

@Component
public class PreSendSettingPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 7694409095471448007L;

	@Autowired
	private AppConfig appConfig;

	private final SubjectPanel subjectPanel = new SubjectPanel("Replace Subject", 20, false);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new LineBorder(Color.RED));

		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			labelPanel.add(new JLabel("Pre Send Setting"));
		}
		this.add(labelPanel);
		this.add(this.subjectPanel);
	}

	public PreSendSettingData getPreSendSettingData() {
		PreSendSettingData preSendSettingData = new PreSendSettingData();
		preSendSettingData.setReplaceSubjectData(this.subjectPanel.getSubjectData());
		return preSendSettingData;
	}

	@Override
	public void loadConfig() {
		PreSendSettingData preSendSettingData = new Gson().fromJson(this.appConfig.getUnsplitString("preSendSetting.data"), PreSendSettingData.class);
		if (preSendSettingData == null) {
			return;
		}
		SubjectData replaceSubjectData = preSendSettingData.getReplaceSubjectData();
		if (replaceSubjectData != null) {
			this.subjectPanel.setSubjectData(replaceSubjectData);
		}
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("preSendSetting.data", new Gson().toJson(this.getPreSendSettingData()));
	}
}
