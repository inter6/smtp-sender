package com.inter6.mail.gui.menu.file;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.service.TabComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class SaveConfigAsMenuItem extends JMenuItem implements ActionListener {

	@Autowired
	private SaveConfigMenuItem saveConfigMenuItem;

	@Autowired
	private TabComponentService tabComponentService;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("Save Config As...");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			LogPanel logPanel = tabComponentService.getActiveTabComponent(LogPanel.class);
			logPanel.info("save config cancel.");
			return;
		}
		saveConfigMenuItem.saveConfig(fileChooser.getSelectedFile());
	}
}
