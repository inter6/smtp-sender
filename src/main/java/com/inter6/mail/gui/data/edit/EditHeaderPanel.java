package com.inter6.mail.gui.data.edit;

import java.awt.Color;
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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.component.HeaderPanel;
import com.inter6.mail.model.component.HeaderData;

@Component
@Scope("prototype")
public class EditHeaderPanel extends JPanel {
	private static final long serialVersionUID = -4212866465171426774L;

	private final List<HeaderPanel> headerPanels = new ArrayList<HeaderPanel>();

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new LineBorder(Color.GREEN));

		this.add(this.createHeaderPanels());

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			JButton addButton = new JButton("Add");
			addButton.addActionListener(this.addEvent);
			actionPanel.add(addButton);
		}
		this.add(actionPanel);
	}

	private JPanel createHeaderPanels() {
		JPanel wrapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			HeaderPanel headerPanel = new HeaderPanel(true);
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
			EditHeaderPanel.this.add(EditHeaderPanel.this.createHeaderPanels(), EditHeaderPanel.this.getComponentCount() - 1);
			EditHeaderPanel.this.updateUI();
		}
	};

	private final ActionListener removeEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			EditHeaderPanel.this.headerPanels.remove(source.getParent());
			EditHeaderPanel.this.remove(source.getParent());
			EditHeaderPanel.this.updateUI();
		}
	};

	public List<HeaderData> getHeaderDatas() {
		List<HeaderData> headerDatas = new ArrayList<HeaderData>();
		for (HeaderPanel headerPanel : this.headerPanels) {
			headerDatas.add(headerPanel.getHeaderData());
		}
		return headerDatas;
	}
}
