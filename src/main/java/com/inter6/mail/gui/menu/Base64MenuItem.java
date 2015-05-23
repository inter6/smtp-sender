package com.inter6.mail.gui.menu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.module.ModuleService;

@Component
public class Base64MenuItem extends JMenuItem implements ActionListener {
	private static final long serialVersionUID = 3869102798552221510L;

	private Base64Dialog base64Dialog;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("Base64 Encoder/Decoder");
		this.addActionListener(this);

		// TODO 구현되면 제거
		this.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.base64Dialog == null || !this.base64Dialog.isVisible()) {
			this.base64Dialog = new Base64Dialog();
		}
		this.base64Dialog.setVisible(true);
	}

	private class Base64Dialog extends JDialog {
		private static final long serialVersionUID = -6824166515619216301L;

		private final JTextArea decodeTextArea = new JTextArea(10, 30);
		private final JTextArea encodeTextArea = new JTextArea(10, 30);
		private final JTextField charsetField = new JTextField("UTF-8", 8);

		private Base64Dialog() {
			super(ModuleService.getBean(MainFrame.class));
			this.setTitle("Base64 Encoder/Decoder");
			this.setSize(600, 400);
			this.initLayout();
		}

		private void initLayout() {
			this.setLayout(new BorderLayout());
			this.add(new JScrollPane(this.decodeTextArea), BorderLayout.NORTH);
			JPanel actionPanel = new JPanel(new FlowLayout());
			{
				JButton encodeButton = new JButton("Encode ⬇︎");
				encodeButton.addActionListener(this.encodeAction);
				actionPanel.add(encodeButton);

				JButton decodeButton = new JButton("Decode ⬆︎");
				decodeButton.addActionListener(this.decodeAction);
				actionPanel.add(decodeButton);

				actionPanel.add(this.charsetField);
			}
			this.add(actionPanel, BorderLayout.CENTER);
			this.add(new JScrollPane(this.encodeTextArea), BorderLayout.SOUTH);
		}

		private final ActionListener encodeAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		};

		private final ActionListener decodeAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		};
	}
}
