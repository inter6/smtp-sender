package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.DirSmtpSendJob;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;

@Component
public class DirSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = 3922252624456731149L;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private LogPanel logPanel;

	private final JCheckBox recursiveButton = new JCheckBox("Recursive");
	private final DefaultListModel dirListModel = new DefaultListModel();
	private final JList dirList = new JList(this.dirListModel);
	private File lastSelectDir;

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

			actionPanel.add(this.recursiveButton);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	private final ActionListener addEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(DirSourcePanel.this.lastSelectDir);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(DirSourcePanel.this) == JFileChooser.APPROVE_OPTION) {
				File dir = fileChooser.getSelectedFile();
				if (dir.isDirectory()) {
					DirSourcePanel.this.dirListModel.addElement(dir);
					DirSourcePanel.this.lastSelectDir = dir;
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
	public Job buildSendJob() {
		DirSmtpSendJob dirSmtpSendJob = ModuleService.getBean(DirSmtpSendJob.class);
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < this.dirListModel.size(); i++) {
			files.add((File) this.dirListModel.get(i));
		}
		dirSmtpSendJob.getData().put("source.dir.dirs", files);
		dirSmtpSendJob.getData().put("source.dir.recursive", this.recursiveButton.isSelected());
		return dirSmtpSendJob;
	}

	@Override
	public void loadConfig() {
		this.dirListModel.removeAllElements();
		String dirPaths[] = this.appConfig.getStringArray("source.dir.dirs");
		if (ArrayUtils.isNotEmpty(dirPaths)) {
			for (String dirPath : dirPaths) {
				if (StringUtils.isBlank(dirPath)) {
					continue;
				}
				File dir = new File(dirPath);
				if (!dir.exists() || !dir.isDirectory()) {
					this.logPanel.info("not found dir ! - DIR:" + dir);
					continue;
				}
				this.dirListModel.addElement(dir);
				this.lastSelectDir = dir;
			}
		}
		this.recursiveButton.setSelected(this.appConfig.getBoolean("source.dir.recursive", false));
	}

	@Override
	public void updateConfig() {
		StringBuilder dirs = new StringBuilder();
		for (int i = 0; i < this.dirListModel.size(); i++) {
			File dir = (File) this.dirListModel.get(i);
			dirs.append(dir.getAbsolutePath()).append(",");
		}
		this.appConfig.setProperty("source.dir.dirs", dirs.toString());
		this.appConfig.setProperty("source.dir.recursive", Boolean.toString(this.recursiveButton.isSelected()));
	}
}
