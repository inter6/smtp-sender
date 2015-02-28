package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.stereotype.Component;

import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.job.smtp.MimeSmtpSendJob;
import com.inter6.mail.module.ModuleService;

@Component
public class MimeSourcePanel extends JPanel implements SendJobBuilder {
	private static final long serialVersionUID = -3278717924684919247L;

	private final JTextArea mimeArea = new JTextArea();

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(new JScrollPane(this.mimeArea), BorderLayout.CENTER);
	}

	@Override
	public AbstractSmtpSendJob buildSendJob() throws Throwable {
		MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
		mimeSmtpSendJob.setMessageStream(new ByteArrayInputStream(this.mimeArea.getText().getBytes()));
		return mimeSmtpSendJob;
	}
}
