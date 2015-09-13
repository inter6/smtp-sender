package com.inter6.mail.gui.data;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.module.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class DataPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 1398719678774376633L;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private EnvelopePanel envelopePanel;

	@Autowired
	private EditSourcePanel editSourcePanel;

	@Autowired
	private MimeSourcePanel mimeSourcePanel;

	@Autowired
	private EmlSourcePanel emlSourcePanel;

	private final JRadioButton editSourceButton = new JRadioButton("Editor");
	private final JRadioButton mimeSourceButton = new JRadioButton("MIME");
	private final JRadioButton emlSourceButton = new JRadioButton("EML");
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
				this.editSourceButton.addActionListener(this.sourceSelectChangeEvent);
				this.mimeSourceButton.addActionListener(this.sourceSelectChangeEvent);
				this.emlSourceButton.addActionListener(this.sourceSelectChangeEvent);
				sourceSelectPanel.add(this.editSourceButton);
				sourceSelectPanel.add(this.mimeSourceButton);
				sourceSelectPanel.add(this.emlSourceButton);

				ButtonGroup sourceSelectGroup = new ButtonGroup();
				sourceSelectGroup.add(this.editSourceButton);
				sourceSelectGroup.add(this.mimeSourceButton);
				sourceSelectGroup.add(this.emlSourceButton);

				this.editSourceButton.doClick();
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
			if (sourceButton == DataPanel.this.editSourceButton) {
				DataPanel.this.setSourcePanel(DataPanel.this.editSourcePanel);
			} else if (sourceButton == DataPanel.this.mimeSourceButton) {
				DataPanel.this.setSourcePanel(DataPanel.this.mimeSourcePanel);
			} else if (sourceButton == DataPanel.this.emlSourceButton) {
				DataPanel.this.setSourcePanel(DataPanel.this.emlSourcePanel);
			}
		}
	};

	private void setSourcePanel(JPanel sourcePanel) {
		this.selectedJobBuilder = (SendJobBuilder) sourcePanel;
		this.sourceInputPanel.removeAll();
		this.sourceInputPanel.add(sourcePanel);
		this.sourceInputPanel.updateUI();
	}

	public AbstractSmtpSendJob getSendJob() throws Throwable {
		return this.selectedJobBuilder.buildSendJob();
	}

	@Override
	public void loadConfig() {
		String sourcePanel = this.appConfig.getString("source.type");
		if ("edit".equals(sourcePanel)) {
			this.editSourceButton.doClick();
		} else if ("mime".equals(sourcePanel)) {
			this.mimeSourceButton.doClick();
		} else if ("eml".equals(sourcePanel)) {
			this.emlSourceButton.doClick();
		} else {
			this.editSourceButton.doClick();
		}
	}

	@Override
	public void updateConfig() {
		if (this.editSourceButton.isSelected()) {
			this.appConfig.setProperty("source.type", "edit");
		} else if (this.mimeSourceButton.isSelected()) {
			this.appConfig.setProperty("source.type", "mime");
		} else if (this.emlSourceButton.isSelected()) {
			this.appConfig.setProperty("source.type", "eml");
		}
	}
}
