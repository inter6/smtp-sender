package com.inter6.mail.gui.menu.tools;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.module.ModuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeUtility;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class Rfc2074MenuItem extends JMenuItem implements ActionListener {
	private static final long serialVersionUID = 3869102798552221510L;

	private Rfc2074Dialog rfc2074Dialog;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("RFC2047 Encoder/Decoder");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.rfc2074Dialog == null || !this.rfc2074Dialog.isVisible()) {
			this.rfc2074Dialog = new Rfc2074Dialog();
		}
		this.rfc2074Dialog.setVisible(true);
	}

	private static class Rfc2074Dialog extends JDialog {
		private static final long serialVersionUID = 5320394394074238399L;

		private final JTextArea decodeTextArea = new JTextArea(9, 30);
		private final JTextArea encodeTextArea = new JTextArea(9, 30);
		private final JTextField charsetField = new JTextField("UTF-8", 8);
		private final JComboBox<String> encodingOptionBox = new JComboBox<>(new String[]{"B", "Q"});

		private Rfc2074Dialog() {
			super(ModuleService.getBean(MainFrame.class));
			this.setTitle("RFC2047 Encoder/Decoder");
			this.setSize(600, 400);
			this.setResizable(false);
			this.initLayout();
		}

		private void initLayout() {
			this.setLayout(new BorderLayout());
			this.add(new JScrollPane(this.decodeTextArea), BorderLayout.NORTH);
			JPanel actionPanel = new JPanel(new FlowLayout());
			{
				JButton encodeButton = new JButton("Encode ▼");
				encodeButton.addActionListener(this.createEncodeAction());
				actionPanel.add(encodeButton);

				JButton decodeButton = new JButton("Decode ▲");
				decodeButton.addActionListener(this.createDecodeAction());
				actionPanel.add(decodeButton);

				actionPanel.add(this.charsetField);
				actionPanel.add(this.encodingOptionBox);
			}
			this.add(actionPanel, BorderLayout.CENTER);
			this.add(new JScrollPane(this.encodeTextArea), BorderLayout.SOUTH);
		}

		private ActionListener createEncodeAction() {
			return new ActionListener() {

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
		}

		private ActionListener createDecodeAction() {
			return new ActionListener() {

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
}
