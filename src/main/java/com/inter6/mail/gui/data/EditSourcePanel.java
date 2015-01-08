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

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.SubjectPanel;
import com.inter6.mail.gui.data.edit.EditAddressPanel;
import com.inter6.mail.gui.data.edit.EditMessagePanel;
import com.inter6.mail.job.Job;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.MimeSmtpSendJob;
import com.inter6.mail.model.component.AddressData;
import com.inter6.mail.model.component.SubjectData;
import com.inter6.mail.module.ModuleService;

@Component
public class EditSourcePanel extends JPanel implements SendJobBuilder {
	private static final long serialVersionUID = -4373325495997044386L;

	private final SubjectPanel subjectPanel = new SubjectPanel("Subject", true);

	@Autowired
	private EditAddressPanel editAddressPanel;

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
				// TODO 원문보기 팝업창
				EditSourcePanel.this.logPanel.infoAndShowDialog(new String(message));
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
	}

	@Override
	public Job buildSendJob() throws Throwable {
		MimeSmtpSendJob mimeSmtpSendJob = ModuleService.getBean(MimeSmtpSendJob.class);
		mimeSmtpSendJob.setMessageStream(new ByteArrayInputStream(this.buildMessage()));
		return mimeSmtpSendJob;
	}
}
