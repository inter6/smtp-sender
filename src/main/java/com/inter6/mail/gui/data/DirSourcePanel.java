package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;

@Component
public class DirSourcePanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 3922252624456731149L;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel dirPanel = new JPanel(new BorderLayout());
		{
			dirPanel.add(new JLabel("Directories"), BorderLayout.NORTH);

			JList<File> dirList = new JList<File>();
			JScrollPane dirListScrollPane = new JScrollPane(dirList);
			dirPanel.add(dirListScrollPane, BorderLayout.CENTER);

			JPanel actionPanel = new JPanel();
			actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
			{
				JButton addButton = new JButton("Add");
				JButton removeButton = new JButton("Remove");
				actionPanel.add(addButton);
				actionPanel.add(removeButton);

				JCheckBox recursiveButton = new JCheckBox("Recursive");
				actionPanel.add(recursiveButton);
			}
			dirPanel.add(actionPanel, BorderLayout.EAST);
		}
		this.add(dirPanel, BorderLayout.NORTH);

		JPanel emlPanel = new JPanel(new BorderLayout());
		{
			emlPanel.add(new JLabel("EMLs on Directories"), BorderLayout.NORTH);

			JList<File> emlList = new JList<File>();
			JScrollPane emlListScrollPane = new JScrollPane(emlList);
			emlPanel.add(emlListScrollPane, BorderLayout.CENTER);
		}
		this.add(emlPanel, BorderLayout.CENTER);
	}

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
