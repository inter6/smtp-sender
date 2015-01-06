package com.inter6.mail.gui.data.edit;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.component.ContentPanel;
import com.inter6.mail.model.ContentType;

@Component
public class EditMessagePanel extends JPanel {
	private static final long serialVersionUID = 3155803701798638117L;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(ContentPanel.createPanel(ContentType.MULTIPART_MIXED, 0), BorderLayout.CENTER);
	}
}
