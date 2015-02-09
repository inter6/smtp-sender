package com.inter6.mail.gui.data.edit;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.component.HeaderPanel;
import com.inter6.mail.model.component.HeaderData;
import com.inter6.mail.model.data.edit.EditHeaderData;

@Component
@Scope("prototype")
public class EditHeaderPanel extends JPanel {
	private static final long serialVersionUID = -4212866465171426774L;

	private final List<HeaderPanel> headerPanels = new ArrayList<HeaderPanel>();

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new LineBorder(Color.GREEN));

		this.add(this.createHeaderPanel());

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			JButton addButton = new JButton("Add");
			addButton.addActionListener(this.addEvent);
			actionPanel.add(addButton);
		}
		this.add(actionPanel);
	}

	private JPanel createHeaderPanel() {
		return this.createHeaderWrapPanel(new HeaderPanel(true));
	}

	private JPanel createHeaderPanel(HeaderData headerData) {
		HeaderPanel headerPanel = new HeaderPanel(false);
		headerPanel.setHeaderData(headerData);
		return this.createHeaderWrapPanel(headerPanel);
	}

	private JPanel createHeaderWrapPanel(HeaderPanel headerPanel) {
		JPanel wrapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			this.headerPanels.add(headerPanel);
			wrapPanel.add(headerPanel);

			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(this.removeEvent);
			wrapPanel.add(removeButton);
		}
		return wrapPanel;
	}

	private final ActionListener addEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			EditHeaderPanel.this.add(EditHeaderPanel.this.createHeaderPanel(), EditHeaderPanel.this.getComponentCount() - 1);
			EditHeaderPanel.this.updateUI();
		}
	};

	private final ActionListener removeEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Container wrapPanel = ((JButton) e.getSource()).getParent();
			EditHeaderPanel.this.headerPanels.remove(wrapPanel.getComponent(0));
			EditHeaderPanel.this.remove(wrapPanel);
			EditHeaderPanel.this.updateUI();
		}
	};

	public List<HeaderData> getHeaderDatas() {
		List<HeaderData> headerDatas = new ArrayList<HeaderData>();
		for (HeaderPanel headerPanel : this.headerPanels) {
			HeaderData headerData = headerPanel.getHeaderData();
			if (StringUtils.isBlank(headerData.getKey())) {
				continue;
			}
			headerDatas.add(headerData);
		}
		return headerDatas;
	}

	public EditHeaderData getEditHeaderData() {
		EditHeaderData editHeaderData = new EditHeaderData();
		editHeaderData.setHeaderDatas(this.getHeaderDatas());
		return editHeaderData;
	}

	public void setEditHeaderData(EditHeaderData editHeaderData) {
		for (HeaderPanel headerPanel : this.headerPanels) {
			this.remove(headerPanel.getParent());
		}
		this.headerPanels.clear();

		if (editHeaderData == null || CollectionUtils.isEmpty(editHeaderData.getHeaderDatas())) {
			this.add(this.createHeaderPanel(), this.getComponentCount() - 1);
		} else {
			for (HeaderData headerData : editHeaderData.getHeaderDatas()) {
				this.add(this.createHeaderPanel(headerData), this.getComponentCount() - 1);
			}
		}
		this.updateUI();

	}
}
