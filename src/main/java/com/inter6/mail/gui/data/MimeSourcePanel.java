package com.inter6.mail.gui.data;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.job.smtp.MimeSmtpSendJob;
import com.inter6.mail.module.ModuleService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;

@Component
public class MimeSourcePanel extends JPanel implements SendJobBuilder {
	private static final long serialVersionUID = -3278717924684919247L;

	private final JTextArea mimeArea = new JTextArea();

	@Autowired
	private LogPanel logPanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(new JScrollPane(this.mimeArea), BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton saveButton = new JButton("Save");
			saveButton.addActionListener(this.createSaveEvent());
			actionPanel.add(saveButton);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	private ActionListener createSaveEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					byte[] message = MimeSourcePanel.this.mimeArea.getText().getBytes("UTF-8");
					JFileChooser fileChooser = new JFileChooser();
					if (fileChooser.showSaveDialog(MimeSourcePanel.this) != JFileChooser.APPROVE_OPTION) {
						MimeSourcePanel.this.logPanel.info("save to eml cancel.");
						return;
					}
					File saveFile = fileChooser.getSelectedFile();
					FileUtils.writeByteArrayToFile(saveFile, message);
					MimeSourcePanel.this.logPanel.info("save to eml success - FILE:" + saveFile);
				} catch (Throwable e) {
					MimeSourcePanel.this.logPanel.error("save to eml fail ! - ", e);
				}
			}
		};
	}

	@Override
	public AbstractSmtpSendJob buildSendJob() throws Throwable {
		MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
		mimeSmtpSendJob.setMessageStream(new ByteArrayInputStream(this.mimeArea.getText().getBytes("UTF-8")));
		return mimeSmtpSendJob;
	}
}
