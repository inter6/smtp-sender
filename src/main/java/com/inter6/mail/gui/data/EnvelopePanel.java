package com.inter6.mail.gui.data;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.module.AppConfig;

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

	public Map<String, Object> getData() throws Throwable {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("envelope.from", this.fromFiled.getText());
		data.put("envelope.to", this.splitToSet(this.toArea.getText(), "\n"));
		return data;
	}

	private Set<String> splitToSet(String str, String separator) {
		Set<String> set = new HashSet<String>();
		if (StringUtils.isBlank(str)) {
			return set;
		}
		String[] tokens = str.split(separator);
		if (ArrayUtils.isNotEmpty(tokens)) {
			for (String token : tokens) {
				set.add(token);
			}
		}
		return set;
	}

	@Override
	public void loadConfig() {
		this.fromFiled.setText(this.appConfig.getString("envelope.from"));
		this.toArea.setText(this.appConfig.getUnsplitString("envelope.to"));
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("envelope.from", this.fromFiled.getText());
		this.appConfig.setProperty("envelope.to", this.toArea.getText());
	}
}
