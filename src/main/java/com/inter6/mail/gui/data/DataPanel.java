package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.module.AppConfig;

@Component
public class DataPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 1398719678774376633L;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private EnvelopePanel envelopePanel;

	@Autowired
	private MimeSourcePanel mimeSourcePanel;

	@Autowired
	private EmlSourcePanel emlSourcePanel;

	@Autowired
	private DirSourcePanel dirSourcePanel;

	private final JRadioButton mimeSourceButton = new JRadioButton("MIME");
	private final JRadioButton emlSourceButton = new JRadioButton("EML");
	private final JRadioButton dirSourceButton = new JRadioButton("EMLs on Dir");
	private final JPanel sourceInputPanel = new JPanel(new BorderLayout());
	private SendJobBuilder selectedJobBuilder;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(this.envelopePanel, BorderLayout.NORTH);

		JPanel sourcePanel = new JPanel(new BorderLayout());
		{
			JPanel sourceSelectPanel = new JPanel(new FlowLayout());
			{
				this.mimeSourceButton.addActionListener(this.sourceSelectChangeEvent);
				this.emlSourceButton.addActionListener(this.sourceSelectChangeEvent);
				this.dirSourceButton.addActionListener(this.sourceSelectChangeEvent);
				sourceSelectPanel.add(this.mimeSourceButton);
				sourceSelectPanel.add(this.emlSourceButton);
				sourceSelectPanel.add(this.dirSourceButton);

				ButtonGroup sourceSelectGroup = new ButtonGroup();
				sourceSelectGroup.add(this.mimeSourceButton);
				sourceSelectGroup.add(this.emlSourceButton);
				sourceSelectGroup.add(this.dirSourceButton);

				this.mimeSourceButton.doClick();
			}
			sourcePanel.add(sourceSelectPanel, BorderLayout.NORTH);
			sourcePanel.add(this.sourceInputPanel, BorderLayout.CENTER);
		}
		this.add(sourcePanel, BorderLayout.CENTER);
	}

	private final ActionListener sourceSelectChangeEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object sourceButton = e.getSource();
			if (sourceButton == DataPanel.this.mimeSourceButton) {
				DataPanel.this.setSourcePanel(DataPanel.this.mimeSourcePanel);
			} else if (sourceButton == DataPanel.this.emlSourceButton) {
				DataPanel.this.setSourcePanel(DataPanel.this.emlSourcePanel);
			} else if (sourceButton == DataPanel.this.dirSourceButton) {
				DataPanel.this.setSourcePanel(DataPanel.this.dirSourcePanel);
			}
		}
	};

	private void setSourcePanel(JPanel sourcePanel) {
		this.selectedJobBuilder = (SendJobBuilder) sourcePanel;
		this.sourceInputPanel.removeAll();
		this.sourceInputPanel.add(sourcePanel);
		this.sourceInputPanel.updateUI();
	}

	public Job getSendJob() {
		return this.selectedJobBuilder.buildSendJob();
	}

	@Override
	public void loadConfig() {
		String sourcePanel = this.appConfig.getString("source.type");
		if ("mime".equals(sourcePanel)) {
			this.mimeSourceButton.doClick();
		} else if ("eml".equals(sourcePanel)) {
			this.emlSourceButton.doClick();
		} else if ("dir".equals(sourcePanel)) {
			this.dirSourceButton.doClick();
		}
	}

	@Override
	public void updateConfig() {
		if (this.mimeSourceButton.isSelected()) {
			this.appConfig.setProperty("source.type", "mime");
		} else if (this.emlSourceButton.isSelected()) {
			this.appConfig.setProperty("source.type", "eml");
		} else if (this.dirSourceButton.isSelected()) {
			this.appConfig.setProperty("source.type", "dir");
		}
	}
}
