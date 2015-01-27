package com.inter6.mail.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.TextViewDialog;
import com.inter6.mail.module.ModuleService;

@Component
public class TopMenuBar extends JMenuBar {
	private static final long serialVersionUID = -3782465535113858483L;

	@Autowired
	private LogPanel logPanel;

	@PostConstruct
	private void init() { // NOPMD
		JMenu toolMenu = new JMenu("Tools");
		{
			JMenuItem base64Item = new JMenuItem("TODO Base64 Encoder/Decoder");
			toolMenu.add(base64Item);

			// XXX 구현되면 제거
			base64Item.setEnabled(false);

			JMenuItem rfc2047Item = new JMenuItem("TODO RFC2047 Encoder/Decoder");
			toolMenu.add(rfc2047Item);

			// XXX 구현되면 제거
			rfc2047Item.setEnabled(false);

			JMenuItem dnsItem = new JMenuItem("TODO DNS Query");
			toolMenu.add(dnsItem);

			// XXX 구현되면 제거
			dnsItem.setEnabled(false);
		}
		this.add(toolMenu);

		// TODO 플러그인 메뉴 구현
		JMenu daouMenu = new JMenu("Daou Tech Only");
		{
			JMenuItem todoItem = new JMenuItem("TODO 추가 jar 로딩 기능 구현");
			todoItem.setEnabled(false);
			daouMenu.add(todoItem);
		}
		this.add(daouMenu);

		JMenu helpMenu = new JMenu("Help");
		{
			JMenuItem todoItem = new JMenuItem("TODO 알아서 잘...");
			todoItem.setEnabled(false);
			helpMenu.add(todoItem);

			JMenuItem aboutItem = new JMenuItem("About smtp-sender");
			aboutItem.addActionListener(this.aboutEvent);
			helpMenu.add(aboutItem);
		}
		this.add(helpMenu);
	}

	private final ActionListener aboutEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
			InputStream msgStream = null;
			try {
				msgStream = ModuleService.getContext().getResource("message/about.msg").getInputStream();
				TextViewDialog.createDialog(IOUtils.toString(msgStream))
						.setModal().setTitle("About smtp-sender").setSize(400, 600).show();
			} catch (IOException e) {
				TopMenuBar.this.logPanel.error("read about message fail !", e);
			} finally {
				IOUtils.closeQuietly(msgStream);
			}
		}
	};
}
