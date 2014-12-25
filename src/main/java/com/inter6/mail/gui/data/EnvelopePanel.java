package com.inter6.mail.gui.data;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;

@Component
public class EnvelopePanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 72285172570878291L;

	private final JTextField fromFiled = new JTextField(20);
	private final JTextField toField = new JTextField(20);

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
			toPanel.add(this.toField);
		}
		this.add(toPanel);
	}

	public Map<String, Object> getData() throws Throwable {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("envelope.from", this.fromFiled.getText());
		data.put("envelope.to", this.toField.getText());
		return data;
	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
