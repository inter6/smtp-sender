package com.inter6.mail.gui.data.edit;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.component.MultiPartPanel;

@Component
public class EditMessagePanel extends JPanel {
	private static final long serialVersionUID = 3155803701798638117L;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(new MultiPartPanel("mixed", 0), BorderLayout.CENTER);
	}
}
