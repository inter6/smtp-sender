package com.inter6.mail.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;

import com.inter6.mail.model.ContentType;

@Slf4j
public abstract class ContentPanel extends JPanel {
	private static final long serialVersionUID = -3928978805796944620L;

	protected final String subType;
	private final int nested;

	private final List<ChildWrapPanel> childPanels = new ArrayList<ContentPanel.ChildWrapPanel>();

	private final JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JComboBox childTypeSelectBox = new JComboBox();
	private final JButton addChildButton = new JButton("Add Child");

	public ContentPanel(String subType, Integer nested) {
		super();
		this.subType = subType;
		this.nested = nested;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new CompoundBorder(new EmptyBorder(10, nested * 20, 10, 10), new LineBorder(Color.RED)));

		JPanel wrapPanel = new JPanel();
		{
			this.initLayout(wrapPanel);
		}
		this.add(wrapPanel);

		// JPanel actionPanel
		{
			this.actionPanel.add(this.childTypeSelectBox);
			this.addChildButton.addActionListener(this.addChildEvent);
			this.actionPanel.add(this.addChildButton);
			this.setActionComponents();
		}
		this.add(this.actionPanel);
	}

	private void setActionComponents() {
		Vector<ContentType> availableChildTypes = this.getAvailableChildTypes(this.childPanels);
		if (CollectionUtils.isNotEmpty(availableChildTypes)) {
			this.childTypeSelectBox.setModel(new DefaultComboBoxModel(availableChildTypes));
			this.actionPanel.setVisible(true);
		} else {
			this.actionPanel.setVisible(false);
		}
	}

	private void addChildPanel() throws Exception {
		ContentType childType = (ContentType) this.childTypeSelectBox.getSelectedItem();
		ChildWrapPanel childContentPanel = new ChildWrapPanel(childType.createPanel(this.nested + 1));

		this.childPanels.add(childContentPanel);
		this.add(childContentPanel, this.getComponentCount() - 1);
		this.setActionComponents();
	}

	private void removeChildPanel(ChildWrapPanel childPanel) {
		this.remove(childPanel);
		this.childPanels.remove(childPanel);
		this.setActionComponents();
	}

	private final ActionListener addChildEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				ContentPanel.this.addChildPanel();
			} catch (Exception e) {
				log.error("add child panel fail !", e);
			}
		}
	};

	protected abstract void initLayout(JPanel wrapPanel);

	protected abstract Vector<ContentType> getAvailableChildTypes(List<ChildWrapPanel> addedChildPanels);

	protected class ChildWrapPanel extends JPanel {
		private static final long serialVersionUID = -3478585608809305772L;

		public ChildWrapPanel(ContentPanel childPanel) {
			super();

			this.setLayout(new BorderLayout());

			JPanel actionPanel = new JPanel();
			actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
			actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
			{
				JButton upButton = new JButton("Up");
				JButton downButton = new JButton("Down");
				JButton removeButton = new JButton("Remove");
				removeButton.addActionListener(this.removeEvent);
				actionPanel.add(upButton);
				actionPanel.add(downButton);
				actionPanel.add(removeButton);
			}
			this.add(actionPanel, BorderLayout.EAST);
			this.add(childPanel, BorderLayout.CENTER);
		}

		private final ActionListener removeEvent = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ContentPanel.this.removeChildPanel(ChildWrapPanel.this);
			}
		};
	}
}
