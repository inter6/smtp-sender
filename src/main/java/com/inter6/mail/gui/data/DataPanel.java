package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.ConfigObserver;

@Component
public class DataPanel extends JPanel implements ConfigObserver {
	private static final long serialVersionUID = 1398719678774376633L;

	@Autowired
	private EnvelopePanel envelopePanel;

	@Autowired
	private MimeSourcePanel mimeSourcePanel;

	@Autowired
	private EmlSourcePanel emlSourcePanel;

	@Autowired
	private DirSourcePanel dirSourcePanel;

	private final JRadioButton mimeSourceButton = new JRadioButton("MIME");
	private final JRadioButton emlSourceButton = new JRadioButton("EML");
	private final JRadioButton dirSourceButton = new JRadioButton("EMLs on Dir");
	private final JPanel sourceInputPanel = new JPanel(new BorderLayout());

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		this.add(this.envelopePanel, BorderLayout.NORTH);

		JPanel sourcePanel = new JPanel(new BorderLayout());
		{
			JPanel sourceSelectPanel = new JPanel(new FlowLayout());
			{
				this.mimeSourceButton.addActionListener(this.sourceSelectChangeEvent);
				this.emlSourceButton.addActionListener(this.sourceSelectChangeEvent);
				this.dirSourceButton.addActionListener(this.sourceSelectChangeEvent);
				sourceSelectPanel.add(this.mimeSourceButton);
				sourceSelectPanel.add(this.emlSourceButton);
				sourceSelectPanel.add(this.dirSourceButton);

				ButtonGroup sourceSelectGroup = new ButtonGroup();
				sourceSelectGroup.add(this.mimeSourceButton);
				sourceSelectGroup.add(this.emlSourceButton);
				sourceSelectGroup.add(this.dirSourceButton);

				this.mimeSourceButton.doClick();
			}
			sourcePanel.add(sourceSelectPanel, BorderLayout.NORTH);
			sourcePanel.add(this.sourceInputPanel, BorderLayout.CENTER);
		}
		this.add(sourcePanel, BorderLayout.CENTER);
	}

	private final ActionListener sourceSelectChangeEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			DataPanel.this.sourceInputPanel.removeAll();
			Object sourceButton = e.getSource();
			if (sourceButton == DataPanel.this.mimeSourceButton) {
				DataPanel.this.sourceInputPanel.add(DataPanel.this.mimeSourcePanel);
			} else if (sourceButton == DataPanel.this.emlSourceButton) {
				DataPanel.this.sourceInputPanel.add(DataPanel.this.emlSourcePanel);
			} else if (sourceButton == DataPanel.this.dirSourceButton) {
				DataPanel.this.sourceInputPanel.add(DataPanel.this.dirSourcePanel);
			}
			DataPanel.this.sourceInputPanel.updateUI();
		}
	};

	@Override
	public void updateConfig() {
		// TODO Auto-generated method stub

	}
}
