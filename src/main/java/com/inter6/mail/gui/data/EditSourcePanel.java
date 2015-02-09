package com.inter6.mail.gui.data;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.SubjectPanel;
import com.inter6.mail.gui.component.TextViewDialog;
import com.inter6.mail.gui.data.edit.EditAddressPanel;
import com.inter6.mail.gui.data.edit.EditHeaderPanel;
import com.inter6.mail.gui.data.edit.EditMessagePanel;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.MimeSmtpSendJob;
import com.inter6.mail.model.component.AddressData;
import com.inter6.mail.model.component.HeaderData;
import com.inter6.mail.model.component.SubjectData;
import com.inter6.mail.model.component.content.PartData;
import com.inter6.mail.model.component.content.PartDataJsonDeserializer;
import com.inter6.mail.model.data.EditSourceData;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;

@Component
public class EditSourcePanel extends JPanel implements SendJobBuilder, ConfigObserver {
	private static final long serialVersionUID = -4373325495997044386L;

	private final SubjectPanel subjectPanel = new SubjectPanel("Subject", 30, true);

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
			wrapPanel.add(this.editAddressPanel);
			wrapPanel.add(this.editHeaderPanel);
			wrapPanel.add(this.editMessagePanel);
		}
		this.add(new JScrollPane(wrapPanel), BorderLayout.CENTER);

		JPanel actionPanel = new JPanel(new FlowLayout());
		{
			JButton viewButton = new JButton("View");
			viewButton.addActionListener(this.viewEvent);
			actionPanel.add(viewButton);
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

	private byte[] buildMessage() throws Throwable {
		MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
		this.buildMessageHeader(mimeMessage);
		// TODO 최상위 파트를 바꿀 수 있는 기능
		mimeMessage.setContent((Multipart) this.editMessagePanel.buildContentPart());
		mimeMessage.saveChanges();

		ByteArrayOutputStream memStream = new ByteArrayOutputStream();
		mimeMessage.writeTo(memStream);
		return memStream.toByteArray();
	}

	private void buildMessageHeader(MimeMessage mimeMessage) throws MessagingException, UnsupportedEncodingException {
		SubjectData subjectData = this.subjectPanel.getSubjectData();
		if (subjectData.isUse()) {
			mimeMessage.setSubject(subjectData.encodeSubject());
		}

		List<AddressData> addressDatas = this.editAddressPanel.getAddressDatas();
		for (AddressData addressData : addressDatas) {
			if (!addressData.isUse()) {
				continue;
			}
			RecipientType type = addressData.getRecipientType();
			InternetAddress internetAddress = addressData.toInternetAddress();
			if (type == null) {
				mimeMessage.addFrom(new Address[] { internetAddress });
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
	public Job buildSendJob() throws Throwable {
		MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
		mimeSmtpSendJob.setMessageStream(new ByteArrayInputStream(this.buildMessage()));
		return mimeSmtpSendJob;
	}

	private EditSourceData getEditSourceData() {
		EditSourceData editSourceData = new EditSourceData();
		editSourceData.setSubjectData(this.subjectPanel.getSubjectData());
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

		this.subjectPanel.setSubjectData(editSourceData.getSubjectData());
		this.editAddressPanel.setEditAddressData(editSourceData.getEditAddressData());
		this.editHeaderPanel.setEditHeaderData(editSourceData.getEditHeaderData());
		this.editMessagePanel.setEditMessageData(editSourceData.getEditMessageData());
	}

	@Override
	public void updateConfig() {
		this.appConfig.setProperty("edit.source.data", new Gson().toJson(this.getEditSourceData()));
	}
}
