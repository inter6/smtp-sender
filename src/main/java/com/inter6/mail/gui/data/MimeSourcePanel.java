package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.tab.TabComponentPanel;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.job.smtp.MimeSmtpSendJob;
import com.inter6.mail.module.ModuleService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MimeSourcePanel extends TabComponentPanel implements SendJobBuilder {
	private static final long serialVersionUID = -3278717924684919247L;

	private LogPanel logPanel;

	private final JTextArea mimeArea = new JTextArea();

	public MimeSourcePanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	private void init() {
		logPanel = tabComponentManager.getTabComponent(tabName, LogPanel.class);

		this.setLayout(new BorderLayout());

		this.add(mimeArea, BorderLayout.CENTER);

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
					MimeSourcePanel.this.logPanel.error("save to eml fail !", e);
				}
			}
		};
	}

	@Override
	public AbstractSmtpSendJob buildSendJob() throws Throwable {
		MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
		mimeSmtpSendJob.setTabName(tabName);
		mimeSmtpSendJob.setMessageStream(new ByteArrayInputStream(this.mimeArea.getText().getBytes("UTF-8")));
		return mimeSmtpSendJob;
	}
}
