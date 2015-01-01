package com.inter6.mail.gui.data;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.component.SubjectPanel;
import com.inter6.mail.gui.data.edit.EditAddressPanel;
import com.inter6.mail.gui.data.edit.EditMessagePanel;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.SendJobBuilder;

@Component
public class EditSourcePanel extends JPanel implements SendJobBuilder {
	private static final long serialVersionUID = -4373325495997044386L;

	private final SubjectPanel subjectPanel = new SubjectPanel("Subject", true);

	@Autowired
	private EditAddressPanel editAddressPanel;

	@Autowired
	private EditMessagePanel editMessagePanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
		{
			wrapPanel.add(this.subjectPanel);
			wrapPanel.add(this.editAddressPanel);
			wrapPanel.add(this.editMessagePanel);
		}
		this.add(new JScrollPane(wrapPanel), BorderLayout.CENTER);
	}

	@Override
	public Job buildSendJob() {
		// TODO Auto-generated method stub
		return null;
	}
}
