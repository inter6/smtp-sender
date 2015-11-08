package com.inter6.mail.gui.menu.help;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.TextViewDialog;
import com.inter6.mail.module.ModuleService;
import com.inter6.mail.module.TabComponentManager;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

@Component
public class AboutMenuItem extends JMenuItem implements ActionListener {
	private static final long serialVersionUID = -567473046293341703L;

	private TabComponentManager tabComponentManager = TabComponentManager.getInstance();

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
			LogPanel logPanel = tabComponentManager.getActiveTabComponent(LogPanel.class);
			logPanel.error("read about message fail !", e);
		} finally {
			IOUtils.closeQuietly(msgStream);
		}
	}
}
