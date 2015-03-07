package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.job.smtp.DirSmtpSendJob;
import com.inter6.mail.model.AppSession;
import com.inter6.mail.model.data.DirSourceData;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;

@Component
public class DirSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = 3922252624456731149L;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private AppSession appSession;

	private final JCheckBox recursiveCheckButton = new JCheckBox("Recursive");
	private final DefaultListModel dirListModel = new DefaultListModel();
	private final JList dirList = new JList(this.dirListModel);

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(new JScrollPane(this.dirList), BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton addButton = new JButton("Add");
			JButton removeButton = new JButton("Remove");
			addButton.addActionListener(this.addEvent);
			removeButton.addActionListener(this.removeEvent);
			actionPanel.add(addButton);
			actionPanel.add(removeButton);

			actionPanel.add(this.recursiveCheckButton);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	private final ActionListener addEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(DirSourcePanel.this.appSession.getLastSelectSourceDir());
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(DirSourcePanel.this) == JFileChooser.APPROVE_OPTION) {
				File dir = fileChooser.getSelectedFile();
				if (dir.isDirectory()) {
					DirSourcePanel.this.dirListModel.addElement(dir.getAbsolutePath());
					DirSourcePanel.this.appSession.setLastSelectSourceDir(dir.getAbsolutePath());
				}
			}
		}
	};

	private final ActionListener removeEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Object dir : DirSourcePanel.this.dirList.getSelectedValues()) {
				DirSourcePanel.this.dirListModel.removeElement(dir);
			}
		}
	};

	@Override
	public AbstractSmtpSendJob buildSendJob() throws Throwable {
		DirSmtpSendJob dirSmtpSendJob = ModuleService.getBean(DirSmtpSendJob.class);
		dirSmtpSendJob.setDirSourceData(this.getDirSourceData());
		return dirSmtpSendJob;
	}

	private DirSourceData getDirSourceData() {
		DirSourceData dirSourceData = new DirSourceData();
		List<File> dirs = new ArrayList<File>();
		for (int i = 0; i < this.dirListModel.size(); i++) {
			dirs.add((File) this.dirListModel.get(i));
		}
		dirSourceData.setDirs(dirs);
		dirSourceData.setRecursive(this.recursiveCheckButton.isSelected());
		return dirSourceData;
	}

	@Override
	public void loadConfig() {
		this.dirListModel.clear();
		DirSourceData dirSourceData = new Gson().fromJson(this.appConfig.getUnsplitString("dir.source.data"), DirSourceData.class);
		if (dirSourceData == null) {
			return;
		}

		Collection<File> dirs = dirSourceData.getDirs();
		if (CollectionUtils.isNotEmpty(dirs)) {
			for (File dir : dirs) {
				this.dirListModel.addElement(dir.getAbsolutePath());
			}
		}
		this.recursiveCheckButton.setSelected(dirSourceData.isRecursive());
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("dir.source.data", new Gson().toJson(this.getDirSourceData()));
	}
}
