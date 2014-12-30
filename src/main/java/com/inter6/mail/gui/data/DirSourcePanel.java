package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.DirSmtpSendJob;
import com.inter6.mail.module.ModuleService;

@Component
public class DirSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = 3922252624456731149L;

	private final JCheckBox recursiveButton = new JCheckBox("Recursive");
	private final DefaultListModel dirListModel = new DefaultListModel();
	private final JList dirList = new JList(this.dirListModel);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(new JLabel("Directories"), BorderLayout.NORTH);
		this.add(new JScrollPane(this.dirList), BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton addButton = new JButton("Add");
			JButton removeButton = new JButton("Remove");
			actionPanel.add(addButton);
			actionPanel.add(removeButton);

			actionPanel.add(this.recursiveButton);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	@Override
	public Job buildSendJob() {
		DirSmtpSendJob dirSmtpSendJob = ModuleService.getBean(DirSmtpSendJob.class);
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < this.dirListModel.size(); i++) {
			files.add((File) this.dirListModel.get(i));
		}
		dirSmtpSendJob.getData().put("dirs", files);
		dirSmtpSendJob.getData().put("recursive", this.recursiveButton.isSelected());
		return dirSmtpSendJob;
	}

	@Override
	public void loadConfig() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
