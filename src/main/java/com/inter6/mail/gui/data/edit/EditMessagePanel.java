package com.inter6.mail.gui.data.edit;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.content.ContentPartPanel;
import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.data.edit.EditMessageData;

@Component
public class EditMessagePanel extends JPanel {
	private static final long serialVersionUID = 3155803701798638117L;

	@Autowired
	private LogPanel logPanel;

	private ContentPartPanel rootPartPanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		// TODO 최상위 파트를 바꿀 수 있는 기능
		try {
			this.rootPartPanel = ContentPartPanel.createPanelByRoot(ContentType.MULTIPART_MIXED);
			this.add(this.rootPartPanel, BorderLayout.CENTER);
		} catch (Exception e) {
			this.logPanel.error("create content panel fail ! - TYPE:" + ContentType.MULTIPART_MIXED, e);
		}
	}

	public Object buildContentPart() throws Throwable {
		return this.rootPartPanel.buildContentPart();
	}

	public EditMessageData getEditMessageData() {
		EditMessageData editMessageData = new EditMessageData();
		editMessageData.setRootPartData(this.rootPartPanel.getPartData());
		return editMessageData;
	}

	public void setEditMessageData(EditMessageData editMessageData) {
		if (editMessageData == null) {
			return;
		}
		this.rootPartPanel.setPartData(editMessageData.getRootPartData());
	}
}
