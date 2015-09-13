package com.inter6.mail.gui.advanced;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.component.DatePanel;
import com.inter6.mail.gui.component.EncodingTextPanel;
import com.inter6.mail.model.advanced.PreSendSettingData;
import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.EncodingTextData;
import com.inter6.mail.module.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.FlowLayout;

@Component
public class PreSendSettingPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 7694409095471448007L;

	@Autowired
	private AppConfig appConfig;

	private final EncodingTextPanel subjectPanel = new EncodingTextPanel("Replace Subject", 20, false);
	private final DatePanel datePanel = new DatePanel("Replace Date", 20, true, true);

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
		this.add(this.datePanel);
	}

	public PreSendSettingData getPreSendSettingData() {
		PreSendSettingData preSendSettingData = new PreSendSettingData();
		preSendSettingData.setReplaceSubjectData(new EncodingTextData()/*this.subjectPanel.getEncodingTextData()*/);
		preSendSettingData.setDateData(new DateData()/*this.datePanel.getDateData()*/);
		return preSendSettingData;
	}

	@Override
	public void loadConfig() {
		PreSendSettingData preSendSettingData = new Gson().fromJson(this.appConfig.getUnsplitString("preSendSetting.data"), PreSendSettingData.class);
		if (preSendSettingData == null) {
			return;
		}
		EncodingTextData replaceSubjectData = preSendSettingData.getReplaceSubjectData();
		if (replaceSubjectData != null) {
			this.subjectPanel.setEncodingTextData(replaceSubjectData);
		}
		DateData replaceDateData = preSendSettingData.getDateData();
		if (replaceDateData != null) {
			this.datePanel.setDateData(replaceDateData);
		}
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("preSendSetting.data", new Gson().toJson(this.getPreSendSettingData()));
	}
}
