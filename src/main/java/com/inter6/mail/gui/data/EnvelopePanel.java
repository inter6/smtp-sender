package com.inter6.mail.gui.data;

import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.model.data.EnvelopeData;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.util.StringUtil;

@Component
public class EnvelopePanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 72285172570878291L;

	@Autowired
	private AppConfig appConfig;

	private final JTextField fromFiled = new JTextField(40);
	private final JTextArea toArea = new JTextArea(5, 40);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel fromPanel = new JPanel(new FlowLayout());
		{
			fromPanel.add(new JLabel("Mail From"));
			fromPanel.add(this.fromFiled);
		}
		this.add(fromPanel);
		JPanel toPanel = new JPanel(new FlowLayout());
		{
			toPanel.add(new JLabel("Rcpt To"));
			toPanel.add(new JScrollPane(this.toArea));
		}
		this.add(toPanel);
	}

	public EnvelopeData getEnvelopeData() {
		EnvelopeData envelopeData = new EnvelopeData();
		envelopeData.setMailFrom(this.fromFiled.getText());
		envelopeData.setRcptTos(StringUtil.splitToSet(this.toArea.getText(), "\n"));
		return envelopeData;
	}

	@Override
	public void loadConfig() {
		EnvelopeData envelopeData = new Gson().fromJson(this.appConfig.getUnsplitString("envelope.data"), EnvelopeData.class);
		if (envelopeData == null) {
			return;
		}
		this.fromFiled.setText(envelopeData.getMailFrom());
		this.toArea.setText(StringUtils.join(envelopeData.getRcptTos(), "\n"));
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("envelope.data", new Gson().toJson(this.getEnvelopeData()));
	}
}
