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
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.SendJobBuilder;
import com.inter6.mail.job.EmlSendJob;
import com.inter6.mail.job.Job;
import com.inter6.mail.module.ModuleService;

@Component
public class EmlSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = -3388480705076640191L;

	private final DefaultListModel<File> emlListModel = new DefaultListModel<File>();
	private final JList<File> emlList = new JList<File>(this.emlListModel);
	private File lastSelectFile;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JScrollPane emlListScrollPane = new JScrollPane(this.emlList);
		this.add(emlListScrollPane, BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton addButton = new JButton("Add");
			JButton removeButton = new JButton("Remove");
			addButton.addActionListener(this.addEvent);
			removeButton.addActionListener(this.removeEvent);
			actionPanel.add(addButton);
			actionPanel.add(removeButton);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	@Override
	public Job buildSendJob() {
		EmlSendJob emlSendJob = ModuleService.getBean(EmlSendJob.class);
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < this.emlListModel.size(); i++) {
			files.add(this.emlListModel.get(i));
		}
		emlSendJob.getData().put("files", files);
		return emlSendJob;
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
			for (File eml : EmlSourcePanel.this.emlList.getSelectedValuesList()) {
				EmlSourcePanel.this.emlListModel.removeElement(eml);
			}
		}
	};

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
