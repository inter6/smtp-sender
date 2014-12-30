package com.inter6.mail.gui.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogPanel extends JPanel {
	private static final long serialVersionUID = 752623058795917575L;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final JTextArea logArea = new JTextArea();
	private final JCheckBox autoScrollCheckBox = new JCheckBox("Auto");

	private final Object appendLock = new Object();

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.logArea.setRows(5);
		this.logArea.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(this.logArea);
		this.add(logScrollPane, BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton clearButton = new JButton("Clear");
			clearButton.addActionListener(this.clearEvent);
			actionPanel.add(clearButton);
			actionPanel.add(this.autoScrollCheckBox);

			this.autoScrollCheckBox.doClick();

			// XXX 구현되면 제거
			this.autoScrollCheckBox.setEnabled(false);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	private final ActionListener clearEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (LogPanel.this.appendLock) {
				LogPanel.this.logArea.setText("");
			}
		}
	};

	public void info(String msg) {
		this.log.info(msg);
		this.append(msg);
	}

	public void infoAndShowDialog(String msg) {
		this.info(msg);
		JOptionPane.showMessageDialog(this, msg);
	}

	public void error(String msg, Throwable e) {
		this.log.error(msg, e);
		this.append(msg + (e != null ? " ERR:" + e.getMessage() : ""));
	}

	public void errorAndShowDialog(String msg, Throwable e) {
		this.error(msg, e);
		JOptionPane.showMessageDialog(this, msg + (e != null ? " ERR:" + e.getMessage() : ""));
	}

	private void append(String msg) {
		synchronized (this.appendLock) {
			if (this.logArea.getLineCount() > 10000) {
				this.logArea.setText("clear log");
			}
			this.logArea.append(msg + "\n");
		}
	}
}
