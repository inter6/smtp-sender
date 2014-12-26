package com.inter6.mail.gui.data;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.module.AppConfig;

@Component
public class EnvelopePanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 72285172570878291L;

	@Autowired
	private AppConfig appConfig;

	private final JTextField fromFiled = new JTextField(20);
	private final JTextArea toArea = new JTextArea(5, 20);
	private final JCheckBox fromReplaceCheckBox = new JCheckBox("Replace from Header");
	private final JCheckBox toReplaceCheckBox = new JCheckBox("Replace from Header");

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel fromPanel = new JPanel(new FlowLayout());
		{
			fromPanel.add(new JLabel("Mail From"));
			fromPanel.add(this.fromFiled);
			fromPanel.add(this.fromReplaceCheckBox);

			// XXX 구현되면 제거
			this.fromReplaceCheckBox.setEnabled(false);
		}
		this.add(fromPanel);
		JPanel toPanel = new JPanel(new FlowLayout());
		{
			toPanel.add(new JLabel("Rcpt To"));
			toPanel.add(new JScrollPane(this.toArea));
			toPanel.add(this.toReplaceCheckBox);

			// XXX 구현되면 제거
			this.toReplaceCheckBox.setEnabled(false);
		}
		this.add(toPanel);
	}

	public Map<String, Object> getData() throws Throwable {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("envelope.from", this.fromFiled.getText());
		data.put("envelope.to", this.toArea.getText());
		return data;
	}

	@Override
	public void loadConfig() {
		this.fromFiled.setText(this.appConfig.getString("envelope.from"));
		this.toArea.setText(this.appConfig.getString("envelope.to"));
		this.fromReplaceCheckBox.setSelected(this.appConfig.getBoolean("envelope.from.replace.fromHeader", false));
		this.toReplaceCheckBox.setSelected(this.appConfig.getBoolean("envelope.to.replace.fromHeader", false));
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("envelope.from", this.fromFiled.getText());
		this.appConfig.setProperty("envelope.to", this.toArea.getText());
		this.appConfig.setProperty("envelope.from.replace.fromHeader", Boolean.toString(this.fromReplaceCheckBox.isSelected()));
		this.appConfig.setProperty("envelope.to.replace.toHeader", Boolean.toString(this.toReplaceCheckBox.isSelected()));
	}
}
