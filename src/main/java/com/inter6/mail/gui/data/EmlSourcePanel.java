package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;

@Component
public class EmlSourcePanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = -3388480705076640191L;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JList<File> emlList = new JList<File>();
		JScrollPane emlListScrollPane = new JScrollPane(emlList);
		this.add(emlListScrollPane, BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton addButton = new JButton("Add");
			JButton removeButton = new JButton("Remove");
			actionPanel.add(addButton);
			actionPanel.add(removeButton);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
