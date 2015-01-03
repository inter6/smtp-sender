package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.EmlSmtpSendJob;
import com.inter6.mail.model.data.EmlSourceData;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;

@Component
public class EmlSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = -3388480705076640191L;

	@Autowired
	private AppConfig appConfig;

	private final DefaultListModel emlListModel = new DefaultListModel();
	private final JList emlList = new JList(this.emlListModel);
	private File lastSelectFile;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(new JScrollPane(this.emlList), BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton addButton = new JButton("Add");
			JButton removeButton = new JButton("Remove");
			JButton dedupAndSortButton = new JButton("Dedup&Sort");
			addButton.addActionListener(this.addEvent);
			removeButton.addActionListener(this.removeEvent);
			dedupAndSortButton.addActionListener(this.dedupAndSortEvent);
			actionPanel.add(addButton);
			actionPanel.add(removeButton);
			actionPanel.add(dedupAndSortButton);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	private final ActionListener addEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(EmlSourcePanel.this.lastSelectFile);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fileChooser.showOpenDialog(EmlSourcePanel.this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file.isFile()) {
					EmlSourcePanel.this.emlListModel.addElement(file);
					EmlSourcePanel.this.lastSelectFile = file;
				}
			}
		}
	};

	private final ActionListener removeEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Object file : EmlSourcePanel.this.emlList.getSelectedValues()) {
				EmlSourcePanel.this.emlListModel.removeElement(file);
			}
		}
	};

	private final ActionListener dedupAndSortEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (EmlSourcePanel.this.emlListModel.isEmpty()) {
				return;
			}
			Set<Object> files = new TreeSet<Object>();
			for (int i = 0; i < EmlSourcePanel.this.emlListModel.size(); i++) {
				files.add(EmlSourcePanel.this.emlListModel.get(i));
			}
			EmlSourcePanel.this.emlListModel.clear();
			for (Object file : files) {
				EmlSourcePanel.this.emlListModel.addElement(file);
			}
		}
	};

	@Override
	public Job buildSendJob() {
		EmlSmtpSendJob emlSmtpSendJob = ModuleService.getBean(EmlSmtpSendJob.class);
		emlSmtpSendJob.setEmlSourceData(this.getEmlSourceData());
		return emlSmtpSendJob;
	}

	private EmlSourceData getEmlSourceData() {
		EmlSourceData emlSourceData = new EmlSourceData();
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < this.emlListModel.size(); i++) {
			files.add((File) this.emlListModel.get(i));
		}
		emlSourceData.setFiles(files);
		return emlSourceData;
	}

	@Override
	public void loadConfig() {
		this.emlListModel.removeAllElements();
		EmlSourceData emlSourceData = new Gson().fromJson(this.appConfig.getUnsplitString("eml.source.data"), EmlSourceData.class);
		if (emlSourceData == null) {
			return;
		}

		Collection<File> files = emlSourceData.getFiles();
		if (CollectionUtils.isNotEmpty(files)) {
			for (File file : files) {
				this.emlListModel.addElement(file);
				this.lastSelectFile = file;
			}
		}
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("eml.source.data", new Gson().toJson(this.getEmlSourceData()));
	}
}
