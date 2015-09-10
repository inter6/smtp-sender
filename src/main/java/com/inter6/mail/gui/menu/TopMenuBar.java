package com.inter6.mail.gui.menu;

import com.inter6.mail.gui.menu.file.LoadConfigMenuItem;
import com.inter6.mail.gui.menu.file.SaveConfigAsMenuItem;
import com.inter6.mail.gui.menu.file.SaveConfigMenuItem;
import com.inter6.mail.gui.menu.help.AboutMenuItem;
import com.inter6.mail.gui.menu.tools.Base64MenuItem;
import com.inter6.mail.gui.menu.tools.DnsMenuItem;
import com.inter6.mail.gui.menu.tools.Rfc2074MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.awt.FlowLayout;

@Component
public class TopMenuBar extends JMenuBar {
	private static final long serialVersionUID = -3782465535113858483L;

	@Autowired
	private LoadConfigMenuItem loadConfigMenuItem;

	@Autowired
	private SaveConfigMenuItem saveConfigMenuItem;

	@Autowired
	private SaveConfigAsMenuItem saveConfigAsMenuItem;

	@Autowired
	private Base64MenuItem base64MenuItem;

	@Autowired
	private Rfc2074MenuItem rfc2074MenuItem;

	@Autowired
	private DnsMenuItem dnsMenuItem;

	@Autowired
	private AboutMenuItem aboutMenuItem;

	private final JLabel configPathLabel = new JLabel("None");

	@PostConstruct
	private void init() { // NOPMD
		JMenu fileMenu = new JMenu("File");
		{
			fileMenu.add(loadConfigMenuItem);
			fileMenu.add(saveConfigMenuItem);
			fileMenu.add(saveConfigAsMenuItem);
		}
		this.add(fileMenu);

		JMenu toolMenu = new JMenu("Tools");
		{
			toolMenu.add(this.base64MenuItem);
			toolMenu.add(this.rfc2074MenuItem);
			toolMenu.add(this.dnsMenuItem);
		}
		this.add(toolMenu);

		JMenu helpMenu = new JMenu("Help");
		{
			helpMenu.add(this.aboutMenuItem);
		}
		this.add(helpMenu);

		JPanel configPathPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		{
			configPathPanel.add(new JLabel("Current Config - "));
			configPathPanel.add(configPathLabel);
		}
		this.add(configPathPanel);
	}

	public void setConfigPath(String path) {
		configPathLabel.setText(path);
	}
}
