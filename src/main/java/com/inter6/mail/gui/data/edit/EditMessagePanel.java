package com.inter6.mail.gui.data.edit;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.component.content.ContentPartPanel;
import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.data.edit.EditMessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

@Component
public class EditMessagePanel extends JPanel {
	private static final long serialVersionUID = 3155803701798638117L;

	@Autowired
	private LogPanel logPanel;

	private final JComboBox<ContentType> rootTypeSelectBox = new JComboBox<>();
	private ContentPartPanel rootPartPanel;

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BorderLayout());

		JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			selectPanel.add(new JLabel("Content-Type: "));
			this.rootTypeSelectBox.setModel(new DefaultComboBoxModel<>(this.getAvailableRootTypes()));
			this.rootTypeSelectBox.addActionListener(this.createChangeRootTypeEvent());
			selectPanel.add(this.rootTypeSelectBox);
		}
		this.add(selectPanel, BorderLayout.NORTH);

		try {
			this.rootPartPanel = ContentPartPanel.createPanelByRoot(ContentType.MULTIPART_MIXED);
			this.add(this.rootPartPanel, BorderLayout.CENTER);
		} catch (Exception e) {
			this.logPanel.error("create content panel fail ! - TYPE:" + ContentType.MULTIPART_MIXED, e);
		}
	}

	private Vector<ContentType> getAvailableRootTypes() {
		Vector<ContentType> childTypes = new Vector<>();
		childTypes.add(ContentType.MULTIPART_MIXED);
		childTypes.add(ContentType.MULTIPART_ALTERNATIVE);
		childTypes.add(ContentType.MULTIPART_RELATED);
		childTypes.add(ContentType.TEXT_PLAIN);
		childTypes.add(ContentType.TEXT_HTML);
		childTypes.add(ContentType.ATTACHMENT);
		return childTypes;
	}

	private ActionListener createChangeRootTypeEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				EditMessagePanel.this.changeRootPartPanel();
			}
		};
	}

	private void changeRootPartPanel() {
		ContentType currentType = this.rootPartPanel.getContentType();
		ContentType selectType = (ContentType) this.rootTypeSelectBox.getSelectedItem();
		if (currentType == selectType) {
			return;
		}

		try {
			ContentPartPanel newPartPanel = ContentPartPanel.createPanelByRoot(selectType);
			this.remove(this.rootPartPanel);
			this.rootPartPanel = newPartPanel;
			this.add(this.rootPartPanel, BorderLayout.CENTER);
			this.getParent().getParent().validate();
		} catch (Exception e) {
			this.logPanel.error("create content panel fail ! - TYPE:" + selectType, e);
		}
	}

	public Object buildContentPart() throws Throwable {
		return this.rootPartPanel.buildContentPart();
	}

	public EditMessageData getEditMessageData() {
		EditMessageData editMessageData = new EditMessageData();
		editMessageData.setRootContentType((ContentType) this.rootTypeSelectBox.getSelectedItem());
		editMessageData.setRootPartData(this.rootPartPanel.getPartData());
		return editMessageData;
	}

	public void setEditMessageData(EditMessageData editMessageData) {
		if (editMessageData == null) {
			return;
		}

		// MARK 이전 버전 config 파일 호환
		if (editMessageData.getRootContentType() != null) {
			this.rootTypeSelectBox.setSelectedItem(editMessageData.getRootContentType());
		}
		this.changeRootPartPanel();
		this.rootPartPanel.setPartData(editMessageData.getRootPartData());
	}
}
