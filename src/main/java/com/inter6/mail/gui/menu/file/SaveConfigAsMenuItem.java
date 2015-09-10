package com.inter6.mail.gui.menu.file;

import com.inter6.mail.gui.action.LogPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class SaveConfigAsMenuItem extends SaveConfigMenuItem {

	@Autowired
	private LogPanel logPanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("Save Config As...");
		this.addActionListener(clickEvent);
	}

	private ActionListener clickEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showSaveDialog(SaveConfigAsMenuItem.this) != JFileChooser.APPROVE_OPTION) {
				SaveConfigAsMenuItem.this.logPanel.info("save config cancel.");
				return;
			}
			SaveConfigAsMenuItem.this.saveConfig(fileChooser.getSelectedFile());
		}
	};
}
