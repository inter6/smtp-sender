package com.inter6.mail.gui.component.content;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inter6.mail.model.ContentType;

public class AttachmentPartPanel extends ContentPartPanel {
	private static final long serialVersionUID = 7919255590937843181L;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final JLabel typeLabel = new JLabel("application/octet-stream");
	private final JTextField contentIdField = new JTextField(25);
	private final JTextField filenameField = new JTextField(20);
	private final JTextField filenameCharsetField = new JTextField("UTF-8", 6);
	private final JComboBox filenameEncodingOptionBox = new JComboBox(new String[] { "B", "Q" });
	private final JComboBox transferOptionBox = new JComboBox(new String[] { "base64", "quoted-printable", "8bit", "7bit", "binary" });
	private final JTextField pathField = new JTextField(30);
	private File lastSelectFile;

	protected AttachmentPartPanel(ContentType contentType, Integer nested) {
		super(contentType, nested);
	}

	@Override
	protected void initLayout() {
		this.wrapPanel.setLayout(new BorderLayout());

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		{
			JPanel contentTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				contentTypePanel.add(new JLabel("Content-Type: "));
				contentTypePanel.add(this.typeLabel);
				contentTypePanel.add(new JLabel("; name={Content-Disposition.filename}"));
			}
			headerPanel.add(contentTypePanel);

			JPanel filenamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				filenamePanel.add(new JLabel("Content-Disposition: attachment; filename="));
				filenamePanel.add(this.filenameField);
				filenamePanel.add(this.filenameCharsetField);
				filenamePanel.add(this.filenameEncodingOptionBox);
			}
			headerPanel.add(filenamePanel);

			JPanel transferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				transferPanel.add(new JLabel("Content-Transfer-Encoding: "));
				transferPanel.add(this.transferOptionBox);
			}
			headerPanel.add(transferPanel);

			JPanel contentIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				contentIdPanel.add(new JLabel("Content-ID: <"));
				contentIdPanel.add(this.contentIdField);
				contentIdPanel.add(new JLabel(">"));
				JButton generateButton = new JButton("Generate");
				generateButton.addActionListener(this.generateCidEvent);
				contentIdPanel.add(generateButton);

				this.setGenerateCid();
			}
			headerPanel.add(contentIdPanel);
		}
		this.wrapPanel.add(headerPanel, BorderLayout.NORTH);

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			this.pathField.setEditable(false);
			actionPanel.add(this.pathField);

			JButton attachButton = new JButton("Attach");
			attachButton.addActionListener(this.attachEvent);
			actionPanel.add(attachButton);
		}
		this.wrapPanel.add(actionPanel, BorderLayout.CENTER);
	}

	private final ActionListener generateCidEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			AttachmentPartPanel.this.setGenerateCid();
		}
	};

	private void setGenerateCid() {
		String contentId = "smtp_sender_attach_" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
		this.contentIdField.setText(contentId);
	}

	private final ActionListener attachEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(AttachmentPartPanel.this.lastSelectFile);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fileChooser.showOpenDialog(AttachmentPartPanel.this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file.isFile()) {
					AttachmentPartPanel.this.filenameField.setText(file.getName());
					AttachmentPartPanel.this.pathField.setText(file.getAbsolutePath());
					AttachmentPartPanel.this.setContentType();
					AttachmentPartPanel.this.lastSelectFile = file;
				}
			}
		}
	};

	private void setContentType() {
		try {
			MimeMultipart part = (MimeMultipart) this.buildContentPart();
			this.typeLabel.setText(part.getContentType());
		} catch (Throwable e) {
			this.typeLabel.setText("application/octet-stream");
			this.log.error("attachment part build fail !", e);
		}
	}

	@Override
	public Object buildContentPart() throws Throwable {
		MimeBodyPart part = new MimeBodyPart();
		File file = new File(this.pathField.getText());
		part.attachFile(file);

		part.setFileName(MimeUtility.encodeWord(StringUtils.defaultString(this.filenameField.getText(), file.getName()),
				StringUtils.defaultString(this.filenameCharsetField.getText(), "UTF-8"),
				(String) this.filenameEncodingOptionBox.getSelectedItem()));

		part.setHeader("Content-Transfer-Encoding", (String) this.transferOptionBox.getSelectedItem());

		String contentId = this.contentIdField.getText();
		if (StringUtils.isNotBlank(contentId)) {
			part.setContentID("<" + contentId + ">");
		}
		return part;
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ContentPartPanel> addedChildPanels) {
		// attach part can not hava childs.
		return null;
	}
}
