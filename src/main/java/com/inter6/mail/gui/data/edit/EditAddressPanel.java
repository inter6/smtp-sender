package com.inter6.mail.gui.data.edit;

import com.inter6.mail.gui.component.AddressPanel;
import com.inter6.mail.gui.tab.TabComponentPanel;
import com.inter6.mail.model.component.AddressData;
import com.inter6.mail.model.data.edit.EditAddressData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EditAddressPanel extends TabComponentPanel {
	private static final long serialVersionUID = -2074315658132902201L;

	private final List<AddressPanel> addressPanels = new ArrayList<>();
	private final JComboBox<String> typeOptionBox = new JComboBox<>(new String[]{"From", "To", "Cc", "Bcc"});

	public EditAddressPanel(String tabName) {
		super(tabName);
	}

	@PostConstruct
	private void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new LineBorder(Color.BLUE));

		this.add(this.createAddressPanel("From"));
		this.add(this.createAddressPanel("To"));

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			this.typeOptionBox.setSelectedItem("To");
			actionPanel.add(this.typeOptionBox);

			JButton addButton = new JButton("Add");
			addButton.addActionListener(this.createAddEvent());
			actionPanel.add(addButton);
		}
		this.add(actionPanel);
	}

	private JPanel createAddressPanel(String type) {
		return this.createAddressWrapPanel(new AddressPanel(type, true));
	}

	private JPanel createAddressPanel(AddressData addressData) {
		AddressPanel addressPanel = new AddressPanel();
		addressPanel.setAddressData(addressData);
		return this.createAddressWrapPanel(addressPanel);
	}

	private JPanel createAddressWrapPanel(AddressPanel addressPanel) {
		JPanel wrapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			this.addressPanels.add(addressPanel);
			wrapPanel.add(addressPanel);

			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(this.createRemoveEvent());
			wrapPanel.add(removeButton);
		}
		return wrapPanel;
	}

	private ActionListener createAddEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String type = (String) EditAddressPanel.this.typeOptionBox.getSelectedItem();
				JPanel addressPanel = EditAddressPanel.this.createAddressPanel(type);
				EditAddressPanel.this.add(addressPanel, EditAddressPanel.this.getComponentCount() - 1);
				EditAddressPanel.this.updateUI();
			}
		};
	}

	private ActionListener createRemoveEvent() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Container wrapPanel = ((JButton) e.getSource()).getParent();
				EditAddressPanel.this.addressPanels.remove(wrapPanel.getComponent(0));
				EditAddressPanel.this.remove(wrapPanel);
				EditAddressPanel.this.updateUI();
			}
		};
	}

	public List<AddressData> getAddressDatas() {
		List<AddressData> addressDatas = new ArrayList<>();
		for (AddressPanel addressPanel : this.addressPanels) {
			AddressData addressData = addressPanel.getAddressData();
			if (StringUtils.isBlank(addressData.getAddress())) {
				continue;
			}
			addressDatas.add(addressData);
		}
		return addressDatas;
	}

	public EditAddressData getEditAddressData() {
		EditAddressData editAddressData = new EditAddressData();
		editAddressData.setAddressDatas(this.getAddressDatas());
		return editAddressData;
	}

	public void setEditAddressData(EditAddressData editAddressData) {
		for (AddressPanel addressPanel : this.addressPanels) {
			this.remove(addressPanel.getParent());
		}
		this.addressPanels.clear();

		if (editAddressData == null || CollectionUtils.isEmpty(editAddressData.getAddressDatas())) {
			this.add(this.createAddressPanel("From"), this.getComponentCount() - 1);
			this.add(this.createAddressPanel("To"), this.getComponentCount() - 1);
		} else {
			for (AddressData addressData : editAddressData.getAddressDatas()) {
				this.add(this.createAddressPanel(addressData), this.getComponentCount() - 1);
			}
		}
		this.updateUI();
	}
}
