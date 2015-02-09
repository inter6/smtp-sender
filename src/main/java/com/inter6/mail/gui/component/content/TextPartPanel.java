package com.inter6.mail.gui.component.content;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;

import javax.mail.internet.MimeBodyPart;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.component.content.PartData;
import com.inter6.mail.model.component.content.TextPartData;

public class TextPartPanel extends ContentPartPanel {
	private static final long serialVersionUID = -5641431122402910873L;

	private final JTextArea textArea = new JTextArea(5, 30);

	protected TextPartPanel(ContentType contentType, Integer nested) {
		super(contentType, nested);
	}

	@Override
	protected void initLayout() {
		this.wrapPanel.setLayout(new BorderLayout());
		this.wrapPanel.add(new JLabel("Content-Type: " + this.contentType), BorderLayout.NORTH);
		this.wrapPanel.add(this.textArea, BorderLayout.CENTER);
	}

	@Override
	public Object buildContentPart() throws Throwable {
		MimeBodyPart part = new MimeBodyPart();
		part.setText(this.textArea.getText(), "UTF-8", this.contentType.getSubType());
		return part;
	}

	@Override
	public PartData getPartData() {
		TextPartData textPartData = new TextPartData();
		textPartData.setContentType(this.contentType);
		textPartData.setText(this.textArea.getText());
		return textPartData;
	}

	@Override
	protected Vector<ContentType> getAvailableChildTypes(List<ContentPartPanel> addedChildPanels) {
		// text part can not hava childs.
		return null;
	}
}
