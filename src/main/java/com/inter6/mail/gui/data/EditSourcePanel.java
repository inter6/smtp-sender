package com.inter6.mail.gui.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.DatePanel;
import com.inter6.mail.gui.component.EncodingTextPanel;
import com.inter6.mail.gui.component.TextViewDialog;
import com.inter6.mail.gui.data.edit.EditAddressPanel;
import com.inter6.mail.gui.data.edit.EditHeaderPanel;
import com.inter6.mail.gui.data.edit.EditMessagePanel;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.job.smtp.MimeSmtpSendJob;
import com.inter6.mail.model.component.AddressData;
import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.EncodingTextData;
import com.inter6.mail.model.component.HeaderData;
import com.inter6.mail.model.component.content.PartData;
import com.inter6.mail.model.component.content.PartDataJsonDeserializer;
import com.inter6.mail.model.component.content.PartDataJsonSerializer;
import com.inter6.mail.model.data.EditSourceData;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

@Component
@Log4j
public class EditSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = -4373325495997044386L;

	private final EncodingTextPanel subjectPanel = new EncodingTextPanel("Subject", 30, true);
	private final DatePanel datePanel = new DatePanel("Date", 30, true, true);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private EditAddressPanel editAddressPanel;

	@Autowired
	private EditHeaderPanel editHeaderPanel;

	@Autowired
	private EditMessagePanel editMessagePanel;

	@Autowired
	private LogPanel logPanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel wrapPanel = new JPanel();
		wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
		{
			wrapPanel.add(this.subjectPanel);
			wrapPanel.add(this.datePanel);
			wrapPanel.add(this.editAddressPanel);
			wrapPanel.add(this.editHeaderPanel);
			wrapPanel.add(this.editMessagePanel);
		}
		this.add(new JScrollPane(wrapPanel), BorderLayout.CENTER);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		{
			JButton viewButton = new JButton("View");
			viewButton.addActionListener(this.viewEvent);
			actionPanel.add(viewButton);

			JButton saveButton = new JButton("Save");
			saveButton.addActionListener(this.saveEvent);
			actionPanel.add(saveButton);
		}
		this.add(actionPanel, BorderLayout.EAST);
	}

	private final ActionListener viewEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				byte[] message = EditSourcePanel.this.buildMessage();
				TextViewDialog.createDialog(new String(message))
						.setModal().setTitle("View MIME text - smtp-sender").setSize(600, 600).show();
			} catch (Throwable e) {
				EditSourcePanel.this.logPanel.error("build mime fail ! - ", e);
			}
		}
	};

	private final ActionListener saveEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				byte[] message = EditSourcePanel.this.buildMessage();
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(EditSourcePanel.this) != JFileChooser.APPROVE_OPTION) {
					EditSourcePanel.this.logPanel.info("save to eml cancel.");
					return;
				}
				File saveFile = fileChooser.getSelectedFile();
				FileUtils.writeByteArrayToFile(saveFile, message);
				EditSourcePanel.this.logPanel.info("save to eml success - FILE:" + saveFile);
			} catch (Throwable e) {
				EditSourcePanel.this.logPanel.error("save to eml fail ! - ", e);
			}
		}
	};

	private byte[] buildMessage() throws Throwable {
		MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
		this.buildMessageHeader(mimeMessage);

		Object contentPart = this.editMessagePanel.buildContentPart();
		if (contentPart instanceof MimeMultipart) {
			mimeMessage.setContent((Multipart) contentPart);
		} else if (contentPart instanceof MimeBodyPart) {
			MimeBodyPart bodyPart = (MimeBodyPart) contentPart;
			mimeMessage.setDataHandler(bodyPart.getDataHandler());
			Enumeration headers = bodyPart.getAllHeaderLines();
			while (headers.hasMoreElements()) {
				mimeMessage.addHeaderLine((String) headers.nextElement());
			}
		}
		mimeMessage.saveChanges();

		ByteArrayOutputStream memStream = new ByteArrayOutputStream();
		mimeMessage.writeTo(memStream);
		return memStream.toByteArray();
	}

	private void buildMessageHeader(MimeMessage mimeMessage) throws MessagingException, UnsupportedEncodingException {
		EncodingTextData subjectData = this.subjectPanel.getEncodingTextData();
		if (subjectData.isUse()) {
			mimeMessage.setSubject(subjectData.encodeSubject());
		}

		DateData dateData = this.datePanel.getDateData();
		if (dateData.isUse()) {
			if (dateData.isNow()) {
				mimeMessage.setSentDate(new Date());
			} else {
				mimeMessage.setHeader("Date", dateData.getText());
			}
		}

		List<AddressData> addressDatas = this.editAddressPanel.getAddressDatas();
		for (AddressData addressData : addressDatas) {
			if (!addressData.isUse()) {
				continue;
			}
			RecipientType type = addressData.getRecipientType();
			InternetAddress internetAddress = addressData.toInternetAddress();
			if (type == null) {
				mimeMessage.addFrom(new Address[]{internetAddress});
			} else {
				mimeMessage.addRecipient(type, internetAddress);
			}
		}

		List<HeaderData> headerDatas = this.editHeaderPanel.getHeaderDatas();
		for (HeaderData headerData : headerDatas) {
			if (!headerData.isUse()) {
				continue;
			}
			mimeMessage.addHeader(headerData.getKey(), headerData.getValue());
		}
	}

	@Override
	public AbstractSmtpSendJob buildSendJob() throws Throwable {
		MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
		mimeSmtpSendJob.setMessageStream(new ByteArrayInputStream(this.buildMessage()));
		return mimeSmtpSendJob;
	}

	private EditSourceData getEditSourceData() {
		EditSourceData editSourceData = new EditSourceData();
		editSourceData.setSubjectData(this.subjectPanel.getEncodingTextData());
		editSourceData.setDateData(this.datePanel.getDateData());
		editSourceData.setEditAddressData(this.editAddressPanel.getEditAddressData());
		editSourceData.setEditHeaderData(this.editHeaderPanel.getEditHeaderData());
		editSourceData.setEditMessageData(this.editMessagePanel.getEditMessageData());
		return editSourceData;
	}

	@Override
	public void loadConfig() {
		Gson gson = new GsonBuilder().registerTypeAdapter(PartData.class, new PartDataJsonDeserializer()).create();
		EditSourceData editSourceData = gson.fromJson(this.appConfig.getUnsplitString("edit.source.data"), EditSourceData.class);
		if (editSourceData == null) {
			return;
		}

		this.subjectPanel.setEncodingTextData(editSourceData.getSubjectData());
		this.datePanel.setDateData(editSourceData.getDateData());
		this.editAddressPanel.setEditAddressData(editSourceData.getEditAddressData());
		this.editHeaderPanel.setEditHeaderData(editSourceData.getEditHeaderData());
		this.editMessagePanel.setEditMessageData(editSourceData.getEditMessageData());
	}

	@Override
	public void updateConfig() {
		Gson gson = new GsonBuilder().registerTypeAdapter(PartData.class, new PartDataJsonSerializer()).create();
		this.appConfig.setProperty("edit.source.data", gson.toJson(this.getEditSourceData()));
	}
}
