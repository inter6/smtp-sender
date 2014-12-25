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
import com.inter6.mail.gui.SendJobBuilder;
import com.inter6.mail.job.DirSendJob;
import com.inter6.mail.job.Job;
import com.inter6.mail.module.ModuleService;

@Component
public class DirSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = 3922252624456731149L;

	private final DefaultListModel<File> dirListModel = new DefaultListModel<File>();
	private final JList<File> dirList = new JList<File>(this.dirListModel);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel dirPanel = new JPanel(new BorderLayout());
		{
			dirPanel.add(new JLabel("Directories"), BorderLayout.NORTH);

			JScrollPane dirListScrollPane = new JScrollPane(this.dirList);
			dirPanel.add(dirListScrollPane, BorderLayout.CENTER);

			JPanel actionPanel = new JPanel();
			actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
			{
				JButton addButton = new JButton("Add");
				JButton removeButton = new JButton("Remove");
				actionPanel.add(addButton);
				actionPanel.add(removeButton);

				JCheckBox recursiveButton = new JCheckBox("Recursive");
				actionPanel.add(recursiveButton);
			}
			dirPanel.add(actionPanel, BorderLayout.EAST);
		}
		this.add(dirPanel, BorderLayout.NORTH);

		JPanel emlPanel = new JPanel(new BorderLayout());
		{
			emlPanel.add(new JLabel("EMLs on Directories"), BorderLayout.NORTH);

			JList<File> emlList = new JList<File>();
			JScrollPane emlListScrollPane = new JScrollPane(emlList);
			emlPanel.add(emlListScrollPane, BorderLayout.CENTER);
		}
		this.add(emlPanel, BorderLayout.CENTER);
	}

	@Override
	public Job buildSendJob() {
		DirSendJob dirSendJob = ModuleService.getBean(DirSendJob.class);
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < this.dirListModel.size(); i++) {
			files.add(this.dirListModel.get(i));
		}
		dirSendJob.getData().put("dirs", files);
		return dirSendJob;
	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
