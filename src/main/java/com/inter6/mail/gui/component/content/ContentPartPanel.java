package com.inter6.mail.gui.component.content;

import com.inter6.mail.gui.action.LogPanel;
import com.inter6.mail.gui.tab.TabComponentPanel;
import com.inter6.mail.model.ContentType;
import com.inter6.mail.model.component.content.PartData;
import com.inter6.mail.module.ModuleService;
import com.inter6.mail.module.TabComponentManager;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public abstract class ContentPartPanel extends TabComponentPanel {
	private static final long serialVersionUID = -3928978805796944620L;

	@Getter
	protected final ContentType contentType;
	private final int nested;

	private final List<ChildWrapPanel> childWrapPanels = new ArrayList<>();

	protected final JPanel wrapPanel = new JPanel();
	private final JPanel childContainerPanel = new JPanel();
	private final JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JComboBox<ContentType> childTypeSelectBox = new JComboBox<>();
	private final JButton addChildButton = new JButton("Add Child Part");

	public static ContentPartPanel createPanel(String tabName, ContentType contentType, int nested) throws Exception {
		Class<? extends ContentPartPanel> panelClass = contentType.getPanelClass();
		ContentPartPanel contentPanel = panelClass.getDeclaredConstructor(String.class, ContentType.class, Integer.class).newInstance(tabName, contentType, nested);
		contentPanel.initLayout();
		return contentPanel;
	}

	public static ContentPartPanel createPanelByRoot(String tabName, ContentType contentType) throws Exception {
		return createPanel(tabName, contentType, 0);
	}

	protected ContentPartPanel(String tabName, ContentType contentType, Integer nested) {
		super(tabName);
		this.contentType = contentType;
		this.nested = nested;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new CompoundBorder(new EmptyBorder(10, nested * 20, 10, 10), new LineBorder(Color.RED)));
		this.add(this.wrapPanel);

		this.childContainerPanel.setLayout(new BoxLayout(this.childContainerPanel, BoxLayout.Y_AXIS));
		this.add(this.childContainerPanel);

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
		Vector<ContentType> availableChildTypes = this.getAvailableChildTypes(this.getUnwrapChildPanels());
		if (CollectionUtils.isNotEmpty(availableChildTypes)) {
			this.childTypeSelectBox.setModel(new DefaultComboBoxModel<>(availableChildTypes));
			this.actionPanel.setVisible(true);
		} else {
			this.actionPanel.setVisible(false);
		}
	}

	protected List<ContentPartPanel> getUnwrapChildPanels() {
		List<ContentPartPanel> childPanels = new ArrayList<>();
		for (ChildWrapPanel childWrapPanel : this.childWrapPanels) {
			childPanels.add(childWrapPanel.contentPanel);
		}
		return childPanels;
	}

	private final ActionListener addChildEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent event) {
			ContentPartPanel.this.addChildPanel((ContentType) ContentPartPanel.this.childTypeSelectBox.getSelectedItem());
		}
	};

	private ContentPartPanel addChildPanel(ContentType childType) {
		try {
			ContentPartPanel childPanel = createPanel(this.tabName, childType, this.nested + 1);
			ChildWrapPanel childWrapPanel = new ChildWrapPanel(childPanel);
			this.childWrapPanels.add(childWrapPanel);
			this.childContainerPanel.add(childWrapPanel);
			this.setActionComponents();
			return childPanel;
		} catch (Exception e) {
			LogPanel logPanel = ModuleService.getBean(TabComponentManager.class).getTabComponent(tabName, LogPanel.class);
			logPanel.error("create content panel fail ! - TYPE:" + childType, e);
			return null;
		}
	}

	protected abstract void initLayout();

	public abstract Object buildContentPart() throws Throwable;

	protected abstract Vector<ContentType> getAvailableChildTypes(List<ContentPartPanel> addedChildPanels);

	public PartData getPartData() {
		PartData partData = this.getPartDataFromComponents();
		partData.setContentType(this.contentType);
		List<PartData> childPartDatas = new ArrayList<>();
		for (ContentPartPanel childPanel : this.getUnwrapChildPanels()) {
			childPartDatas.add(childPanel.getPartData());
		}
		partData.setChildPartDatas(childPartDatas);
		return partData;
	}

	protected abstract PartData getPartDataFromComponents();

	public void setPartData(PartData partData) {
		for (ChildWrapPanel childWrapPanel : new ArrayList<>(this.childWrapPanels)) {
			this.removeChildPanel(childWrapPanel);
		}
		if (partData == null) {
			return;
		}

		this.setComponentsFromPartData(partData);
		if (CollectionUtils.isEmpty(partData.getChildPartDatas())) {
			return;
		}
		for (PartData childPartData : partData.getChildPartDatas()) {
			ContentPartPanel childPartPanel = this.addChildPanel(childPartData.getContentType());
			if (childPartPanel != null) {
				childPartPanel.setPartData(childPartData);
			}
		}
	}

	protected abstract void setComponentsFromPartData(PartData partData);

	protected class ChildWrapPanel extends JPanel {
		private static final long serialVersionUID = -3478585608809305772L;

		private final ContentPartPanel contentPanel;

		public ChildWrapPanel(ContentPartPanel contentPanel) {
			super();
			this.contentPanel = contentPanel;

			this.setLayout(new BorderLayout());

			JPanel actionPanel = new JPanel();
			actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
			actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
			{
				JButton upButton = new JButton("Up");
				JButton downButton = new JButton("Down");
				JButton removeButton = new JButton("Remove");
				upButton.addActionListener(this.createUpEvent());
				downButton.addActionListener(this.createDownEvent());
				removeButton.addActionListener(this.createRemoveEvent());
				actionPanel.add(upButton);
				actionPanel.add(downButton);
				actionPanel.add(removeButton);
			}
			this.add(actionPanel, BorderLayout.EAST);
			this.add(this.contentPanel, BorderLayout.CENTER);
		}

		private ActionListener createUpEvent() {
			return new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ContentPartPanel.this.upChildPanel(ChildWrapPanel.this);
				}

			};
		}

		private ActionListener createDownEvent() {
			return new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ContentPartPanel.this.downChildPanel(ChildWrapPanel.this);
				}

			};
		}

		private ActionListener createRemoveEvent() {
			return new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ContentPartPanel.this.removeChildPanel(ChildWrapPanel.this);
				}
			};
		}
	}

	protected void upChildPanel(ChildWrapPanel childWrapPanel) {
		int index = this.childWrapPanels.indexOf(childWrapPanel);
		if (index == 0) {
			return;
		}
		Collections.swap(this.childWrapPanels, index - 1, index);
		this.childContainerPanel.remove(index);
		this.childContainerPanel.add(childWrapPanel, index - 1);
		this.setActionComponents();
	}

	protected void downChildPanel(ChildWrapPanel childWrapPanel) {
		int index = this.childWrapPanels.indexOf(childWrapPanel);
		if (index >= this.childWrapPanels.size() - 1) {
			return;
		}
		Collections.swap(this.childWrapPanels, index, index + 1);
		this.childContainerPanel.remove(index);
		this.childContainerPanel.add(childWrapPanel, index + 1);
		this.setActionComponents();
	}

	private void removeChildPanel(ChildWrapPanel childPanel) {
		this.childWrapPanels.remove(childPanel);
		this.childContainerPanel.remove(childPanel);
		this.setActionComponents();
	}
}
