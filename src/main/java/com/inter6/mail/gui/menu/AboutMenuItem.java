package com.inter6.mail.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.swing.JMenuItem;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.TextViewDialog;
import com.inter6.mail.module.ModuleService;

@Component
public class AboutMenuItem extends JMenuItem implements ActionListener {
	private static final long serialVersionUID = -567473046293341703L;

	@Autowired
	private LogPanel logPanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("About smtp-sender");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		InputStream msgStream = null;
		try {
			msgStream = ModuleService.getContext().getResource("message/about.msg").getInputStream();
			TextViewDialog.createDialog(IOUtils.toString(msgStream))
					.setModal()
					.setTitle("About smtp-sender")
					.setSize(400, 600)
					.show();
		} catch (IOException e) {
			AboutMenuItem.this.logPanel.error("read about message fail !", e);
		} finally {
			IOUtils.closeQuietly(msgStream);
		}
	}
}