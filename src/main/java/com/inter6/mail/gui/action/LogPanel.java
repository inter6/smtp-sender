package com.inter6.mail.gui.action;

import com.inter6.mail.gui.tab.TabComponentPanel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Date;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class LogPanel extends TabComponentPanel {
	private static final long serialVersionUID = 752623058795917575L;

	private final JTextArea logArea = new JTextArea();
	private final Object appendLock = new Object();
	private boolean isAutoScroll = false;

	public LogPanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	private void init() {
		this.setLayout(new BorderLayout());

		this.logArea.setRows(5);
		this.logArea.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(this.logArea);
		logScrollPane.getVerticalScrollBar().addAdjustmentListener(this.createAutoScrollEvent());
		this.add(logScrollPane, BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton clearButton = new JButton("Clear");
			clearButton.addActionListener(this.createClearEvent());
			actionPanel.add(clearButton);

			JCheckBox autoScrollCheckBox = new JCheckBox("Auto");
			autoScrollCheckBox.addActionListener(this.createAutoScrollCheckEvent());
			actionPanel.add(autoScrollCheckBox);

			autoScrollCheckBox.doClick();
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	private ActionListener createAutoScrollCheckEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LogPanel.this.isAutoScroll = !LogPanel.this.isAutoScroll;
			}
		};
	}

	private AdjustmentListener createAutoScrollEvent() {
		return new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (!LogPanel.this.isAutoScroll) {
					return;
				}
				JScrollBar src = (JScrollBar) e.getSource();
				src.setValue(src.getMaximum());
			}
		};
	}

	private ActionListener createClearEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (LogPanel.this.appendLock) {
					LogPanel.this.logArea.setText("");
				}
			}
		};
	}

	public void info(String msg) {
		log.info(msg);
		this.append(msg);
	}

	public void infoAndShowDialog(String msg) {
		this.info(msg);
		JOptionPane.showMessageDialog(this, msg);
	}

	public void error(String msg, Throwable e) {
		log.error(msg, e);
		this.append(msg + (e != null ? " - ERR:" + e.getMessage() : ""));
	}

	public void errorAndShowDialog(String msg, Throwable e) {
		this.error(msg, e);
		JOptionPane.showMessageDialog(this, msg + (e != null ? " - ERR:" + e.getMessage() : ""));
	}

	private void append(String msg) {
		synchronized (this.appendLock) {
			if (this.logArea.getLineCount() > 10000) {
				this.logArea.setText("clear log");
			}
			this.logArea.append("[" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "] " + msg + "\n");
		}
	}
}
