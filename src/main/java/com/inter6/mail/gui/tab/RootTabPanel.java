package com.inter6.mail.gui.tab;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import lombok.Getter;

import org.springframework.stereotype.Component;

import com.inter6.mail.module.TabComponentManager;

@Component
public class RootTabPanel extends JTabbedPane {

	private TabComponentManager tabComponentManager = TabComponentManager.getInstance();

	@Getter
	private String activeTabName;

	@PostConstruct
	private void init() throws IOException {
		for (int i = 0; i < 10; i++) {
			addTabPanel("tab" + i);
		}
	}

	private void addTabPanel(final String tabName) {
		TabPanel tabPanel = tabComponentManager.getTabComponent(tabName, TabPanel.class);
		tabPanel.addComponentListener(createComponentListener(tabName));
		this.addTab(tabName, tabPanel);
	}

	private ComponentListener createComponentListener(final String tabName) {
		return new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {
				activeTabName = tabName;
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		};
	}
}
