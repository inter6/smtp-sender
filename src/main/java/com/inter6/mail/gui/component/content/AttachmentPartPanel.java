package com.inter6.mail.gui.component.content;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.activation.FileTypeMap;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.inter6.mail.model.AppSession;
import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.component.content.AttachmentPartData;
import com.inter6.mail.model.component.content.PartData;
import com.inter6.mail.module.ModuleService;

public class AttachmentPartPanel extends ContentPartPanel {
	private static final long serialVersionUID = 7919255590937843181L;

	private final JTextField typeField = new JTextField("application/octet-stream", 20);
	private final JCheckBox contentIdUseCheckBox = new JCheckBox();
	private final JTextField contentIdField = new JTextField(25);
	private final JComboBox dispositionOptionBox = new JComboBox(new String[] { "attachment", "inline" });
	private final JTextField filenameField = new JTextField(20);
	private final JTextField filenameCharsetField = new JTextField("UTF-8", 6);
	private final JComboBox filenameEncodingOptionBox = new JComboBox(new String[] { "B", "Q" });
	private final JComboBox transferOptionBox = new JComboBox(new String[] { "base64", "quoted-printable", "8bit", "7bit", "binary" });
	private final JTextField pathField = new JTextField(30);

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
				contentTypePanel.add(this.typeField);
				contentTypePanel.add(new JLabel("; name={Content-Disposition.filename}"));
			}
			headerPanel.add(contentTypePanel);

			JPanel dispositionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				dispositionPanel.add(new JLabel("Content-Disposition: "));
				dispositionPanel.add(this.dispositionOptionBox);
				this.dispositionOptionBox.addActionListener(this.changeDispositionEvent);
				dispositionPanel.add(new JLabel("; filename="));
				dispositionPanel.add(this.filenameField);
				dispositionPanel.add(this.filenameCharsetField);
				dispositionPanel.add(this.filenameEncodingOptionBox);
			}
			headerPanel.add(dispositionPanel);

			JPanel transferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				transferPanel.add(new JLabel("Content-Transfer-Encoding: "));
				transferPanel.add(this.transferOptionBox);
			}
			headerPanel.add(transferPanel);

			JPanel contentIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				contentIdPanel.add(this.contentIdUseCheckBox);
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

	private final ActionListener changeDispositionEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("inline".equalsIgnoreCase((String) AttachmentPartPanel.this.dispositionOptionBox.getSelectedItem())) {
				AttachmentPartPanel.this.contentIdUseCheckBox.setSelected(true);
			}
		}
	};

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
			AppSession appSession = ModuleService.getBean(AppSession.class);
			JFileChooser fileChooser = new JFileChooser(appSession.getLastSelectAttachDir());
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fileChooser.showOpenDialog(AttachmentPartPanel.this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file.isFile()) {
					AttachmentPartPanel.this.filenameField.setText(file.getName());
					AttachmentPartPanel.this.pathField.setText(file.getAbsolutePath());
					AttachmentPartPanel.this.typeField.setText(AttachmentPartPanel.this.getContentType(file));
					appSession.setLastSelectAttachDir(file.getParent());
				}
			}
		}
	};

	private String getContentType(File file) {
		return FileTypeMap.getDefaultFileTypeMap().getContentType(file);
	}

	@Override
	public Object buildContentPart() throws Throwable {
		MimeBodyPart part = new MimeBodyPart();
		File file = new File(this.pathField.getText());
		part.attachFile(file);

		String encodedFilename = MimeUtility.encodeWord(StringUtils.defaultString(this.filenameField.getText(), file.getName()),
				StringUtils.defaultString(this.filenameCharsetField.getText(), "UTF-8"),
				(String) this.filenameEncodingOptionBox.getSelectedItem());

		part.setHeader("Content-Type", this.typeField.getText() + "; name=\"" + encodedFilename + "\"");
		part.setHeader("Content-Disposition", this.dispositionOptionBox.getSelectedItem() + "; filename=\"" + encodedFilename + "\"");
		part.setHeader("Content-Transfer-Encoding", (String) this.transferOptionBox.getSelectedItem());

		if (this.contentIdUseCheckBox.isSelected()) {
			String contentId = this.contentIdField.getText();
			if (StringUtils.isNotBlank(contentId)) {
				part.setContentID("<" + contentId + ">");
			}
		}
		return part;
	}

	@Override
	protected PartData getPartDataFromComponents() {
		AttachmentPartData attachmentPartData = new AttachmentPartData();
		attachmentPartData.setContentTypeStr(this.typeField.getText());
		attachmentPartData.setContentIdUse(this.contentIdUseCheckBox.isSelected());
		attachmentPartData.setContentId(this.contentIdField.getText());
		attachmentPartData.setContentDisposition((String) this.dispositionOptionBox.getSelectedItem());
		attachmentPartData.setContentTransferEncoding((String) this.transferOptionBox.getSelectedItem());
		attachmentPartData.setFilePath(this.pathField.getText());
		attachmentPartData.setFilename(this.filenameField.getText());
		attachmentPartData.setFilenameCharset(this.filenameCharsetField.getText());
		attachmentPartData.setFilenameEncoding((String) this.filenameEncodingOptionBox.getSelectedItem());
		return attachmentPartData;
	}

	@Override
	protected void setComponentsFromPartData(PartData partData) {
		if (!(partData instanceof AttachmentPartData)) {
			throw new IllegalStateException("is not attachment part data ! - DATA:" + partData);
		}
		AttachmentPartData attachmentPartData = (AttachmentPartData) partData;
		this.typeField.setText(attachmentPartData.getContentTypeStr());
		this.contentIdUseCheckBox.setSelected(attachmentPartData.isContentIdUse());
		this.contentIdField.setText(attachmentPartData.getContentId());
		this.dispositionOptionBox.setSelectedItem(attachmentPartData.getContentDisposition());
		this.transferOptionBox.setSelectedItem(attachmentPartData.getContentTransferEncoding());
		this.pathField.setText(attachmentPartData.getFilePath());
		this.filenameField.setText(attachmentPartData.getFilename());
		this.filenameCharsetField.setText(attachmentPartData.getFilenameCharset());
		this.filenameEncodingOptionBox.setSelectedItem(attachmentPartData.getFilenameEncoding());
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ContentPartPanel> addedChildPanels) {
		// attach part can not hava childs.
		return null;
	}
}
