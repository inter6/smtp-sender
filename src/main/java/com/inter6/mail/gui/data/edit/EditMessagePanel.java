package com.inter6.mail.gui.data.edit;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

@Component
public class EditMessagePanel extends JPanel {
	private static final long serialVersionUID = 3155803701798638117L;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
}
