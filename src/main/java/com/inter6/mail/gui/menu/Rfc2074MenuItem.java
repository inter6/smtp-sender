package com.inter6.mail.gui.menu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeUtility;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.module.ModuleService;

@Component
public class Rfc2074MenuItem extends JMenuItem implements ActionListener {
	private static final long serialVersionUID = 3869102798552221510L;

	private Rfc2074Dialog base64Dialog;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("RFC2047 Encoder/Decoder");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.base64Dialog == null || !this.base64Dialog.isVisible()) {
			this.base64Dialog = new Rfc2074Dialog();
		}
		this.base64Dialog.setVisible(true);
	}

	private class Rfc2074Dialog extends JDialog {
		private static final long serialVersionUID = 5320394394074238399L;

		private final JTextArea decodeTextArea = new JTextArea(10, 30);
		private final JTextArea encodeTextArea = new JTextArea(10, 30);
		private final JTextField charsetField = new JTextField("UTF-8", 8);
		private final JComboBox encodingOptionBox = new JComboBox(new String[] { "B", "Q" });

		private Rfc2074Dialog() {
			super(ModuleService.getBean(MainFrame.class));
			this.setTitle("RFC2047 Encoder/Decoder");
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
				actionPanel.add(this.encodingOptionBox);
			}
			this.add(actionPanel, BorderLayout.CENTER);
			this.add(new JScrollPane(this.encodeTextArea), BorderLayout.SOUTH);
		}

		private final ActionListener encodeAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String encodeText = MimeUtility.encodeWord(
							StringUtils.defaultIfEmpty(Rfc2074Dialog.this.decodeTextArea.getText(), ""),
							StringUtils.defaultIfBlank(Rfc2074Dialog.this.charsetField.getText(), "UTF-8"),
							(String) Rfc2074Dialog.this.encodingOptionBox.getSelectedItem());
					Rfc2074Dialog.this.encodeTextArea.setText(encodeText);
				} catch (Throwable e) {
					JOptionPane.showMessageDialog(Rfc2074Dialog.this,
							e.getClass().getSimpleName() + " - " + e.getMessage(),
							"Encoding fail !",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		private final ActionListener decodeAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String encodeText = StringUtils.defaultIfEmpty(Rfc2074Dialog.this.encodeTextArea.getText(), "");
					String decodeText = encodeText;
					if (encodeText.startsWith("=?")) {
						decodeText = MimeUtility.decodeWord(encodeText);
					}
					Rfc2074Dialog.this.decodeTextArea.setText(decodeText);
				} catch (Throwable e) {
					JOptionPane.showMessageDialog(Rfc2074Dialog.this,
							e.getClass().getSimpleName() + " - " + e.getMessage(),
							"Decoding fail !",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};
	}
}
