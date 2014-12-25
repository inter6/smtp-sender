package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.SendJobBuilder;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.MimeSendJob;
import com.inter6.mail.module.ModuleService;

@Component
public class MimeSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = -3278717924684919247L;

	private final JTextArea mimeArea = new JTextArea();

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JScrollPane mimeScrollPane = new JScrollPane(this.mimeArea);
		this.add(mimeScrollPane, BorderLayout.CENTER);
	}

	@Override
	public Job buildSendJob() {
		MimeSendJob mimeSendJob = ModuleService.getBean(MimeSendJob.class);
		mimeSendJob.getData().put("inputStream", new ByteArrayInputStream(this.mimeArea.getText().getBytes()));
		return mimeSendJob;
	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
