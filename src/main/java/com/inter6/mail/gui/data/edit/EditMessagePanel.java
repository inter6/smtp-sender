package com.inter6.mail.gui.data.edit;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.content.ContentPartPanel;
import com.inter6.mail.model.ContentType;

@Component
public class EditMessagePanel extends JPanel {
	private static final long serialVersionUID = 3155803701798638117L;

	@Autowired
	private LogPanel logPanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		try {
			this.add(ContentPartPanel.createPanelByRoot(ContentType.MULTIPART_MIXED), BorderLayout.CENTER);
		} catch (Exception e) {
			this.logPanel.error("create content panel fail ! - TYPE:" + ContentType.MULTIPART_MIXED, e);
		}
	}
}
