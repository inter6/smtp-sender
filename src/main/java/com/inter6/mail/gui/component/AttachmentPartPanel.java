package com.inter6.mail.gui.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.inter6.mail.model.ContentType;

public class AttachmentPartPanel extends ContentPanel {
	private static final long serialVersionUID = 7919255590937843181L;

	// <extension, subType>
	private static final Map<String, String> imageTypeMap = new HashMap<String, String>();
	static {
		imageTypeMap.put("jpg", "jpeg");
		imageTypeMap.put("jpeg", "jpeg");
	}

	private String contentType = "application/octet-stream";
	private final JLabel typeLabel = new JLabel("Content-Type: " + this.contentType);
	private final JTextField pathField = new JTextField(30);
	private File lastSelectFile;

	protected AttachmentPartPanel(String subType, Integer nested) {
		super(subType, nested);
	}

	@Override
	protected void initLayout() {
		this.wrapPanel.setLayout(new BorderLayout());
		this.wrapPanel.add(this.typeLabel, BorderLayout.NORTH);

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			this.pathField.setEditable(false);
			actionPanel.add(this.pathField);

			JButton attachButton = new JButton("Attach");
			attachButton.addActionListener(this.attachEvent);
			actionPanel.add(attachButton);
		}
		this.wrapPanel.add(actionPanel);
	}

	private final ActionListener attachEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(AttachmentPartPanel.this.lastSelectFile);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fileChooser.showOpenDialog(AttachmentPartPanel.this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file.isFile()) {
					AttachmentPartPanel.this.pathField.setText(file.getAbsolutePath());
					AttachmentPartPanel.this.setContentType(file);
					AttachmentPartPanel.this.lastSelectFile = file;
				}
			}
		}
	};

	private void setContentType(File file) {
		String extension = StringUtils.lowerCase(FilenameUtils.getExtension(file.getName()));
		if (imageTypeMap.keySet().contains(extension)) {
			this.contentType = "image/" + imageTypeMap.get(extension);
		} else {
			this.contentType = "application/octet-stream";
		}
		this.typeLabel.setText("Content-Type: " + this.contentType);
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ChildWrapPanel> addedChildPanels) {
		return null;
	}
}
