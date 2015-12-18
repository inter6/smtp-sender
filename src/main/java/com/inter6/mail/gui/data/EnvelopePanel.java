package com.inter6.mail.gui.data;

import java.awt.FlowLayout;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.tab.TabComponentPanel;
import com.inter6.mail.model.data.EnvelopeData;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.util.StringUtil;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EnvelopePanel extends TabComponentPanel implements ConfigObserver {
	private static final long serialVersionUID = 72285172570878291L;

	@Autowired
	private AppConfig appConfig;

	private final JTextField fromField = new JTextField(40);
	private final JTextArea toArea = new JTextArea(3, 40);

	public EnvelopePanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	private void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel fromPanel = new JPanel(new FlowLayout());
		{
			fromPanel.add(new JLabel("Mail From"));
			fromPanel.add(this.fromField);
		}
		this.add(fromPanel);
		JPanel toPanel = new JPanel(new FlowLayout());
		{
			toPanel.add(new JLabel("Rcpt To"));
			toPanel.add(toArea);
		}
		this.add(toPanel);
	}

	public EnvelopeData getEnvelopeData() {
		EnvelopeData envelopeData = new EnvelopeData();
		envelopeData.setMailFrom(this.fromField.getText());
		envelopeData.setRcptTos(StringUtil.splitToSet(this.toArea.getText(), "\n"));
		return envelopeData;
	}

	@Override
	public void loadConfig() {
		EnvelopeData envelopeData = new Gson().fromJson(this.appConfig.getString(tabName + ".envelope.data"), EnvelopeData.class);
		if (envelopeData == null) {
			return;
		}
		this.fromField.setText(envelopeData.getMailFrom());
		this.toArea.setText(StringUtils.join(envelopeData.getRcptTos(), "\n"));
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty(tabName + ".envelope.data", new Gson().toJson(this.getEnvelopeData()));
	}
}
