package com.inter6.mail.gui.component.content;

import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.component.content.PartData;
import com.inter6.mail.model.component.content.TextPartData;

import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class TextPartPanel extends ContentPartPanel {
	private static final long serialVersionUID = -5641431122402910873L;

	private final JTextField charsetField = new JTextField("UTF-8", 6);
	private final JComboBox<String> transferOptionBox = new JComboBox<>(new String[]{"quoted-printable", "8bit", "7bit", "base64", "binary"});
	private final JTextArea textArea = new JTextArea(5, 30);

	protected TextPartPanel(ContentType contentType, Integer nested) {
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
				contentTypePanel.add(new JLabel("Content-Type: " + this.contentType));
				contentTypePanel.add(new JLabel("; charset="));
				contentTypePanel.add(this.charsetField);
			}
			headerPanel.add(contentTypePanel);

			JPanel transferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			{
				transferPanel.add(new JLabel("Content-Transfer-Encoding: "));
				transferPanel.add(this.transferOptionBox);
			}
			headerPanel.add(transferPanel);
		}
		this.wrapPanel.add(headerPanel, BorderLayout.NORTH);

		this.wrapPanel.add(this.textArea, BorderLayout.CENTER);
	}

	@Override
	public Object buildContentPart() throws Throwable {
		MimeBodyPart part = new MimeBodyPart();
		part.setText(this.textArea.getText(), this.charsetField.getText(), this.contentType.getSubType());
		part.setHeader("Content-Transfer-Encoding", (String) this.transferOptionBox.getSelectedItem());
		return part;
	}

	@Override
	protected PartData getPartDataFromComponents() {
		TextPartData textPartData = new TextPartData();
		textPartData.setTextCharset(this.charsetField.getText());
		textPartData.setContentTransferEncoding((String) this.transferOptionBox.getSelectedItem());
		textPartData.setText(this.textArea.getText());
		return textPartData;
	}

	@Override
	protected void setComponentsFromPartData(PartData partData) {
		if (!(partData instanceof TextPartData)) {
			throw new IllegalStateException("is not text part data ! - DATA:" + partData);
		}
		TextPartData textPartData = (TextPartData) partData;
		this.charsetField.setText(textPartData.getTextCharset());
		this.transferOptionBox.setSelectedItem(textPartData.getContentTransferEncoding());
		this.textArea.setText(textPartData.getText());
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ContentPartPanel> addedChildPanels) {
		// text part can not hava childs.
		return null;
	}
}
