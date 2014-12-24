package com.inter6.mail.gui.data;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;

@Component
public class EmlSourcePanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = -3388480705076640191L;

	@PostConstruct
	private void init() { // NOPMD

	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
