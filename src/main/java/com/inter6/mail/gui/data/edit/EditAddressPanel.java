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
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.inter6.mail.gui.component.AddressPanel;
import com.inter6.mail.model.component.AddressData;

@Component
@Scope("prototype")
public class EditAddressPanel extends JPanel {
	private static final long serialVersionUID = -2074315658132902201L;

	private final List<AddressPanel> addressPanels = new ArrayList<AddressPanel>();

	private final JComboBox typeOptionBox = new JComboBox(new String[] { "From", "To", "Cc", "Bcc" });

	@PostConstruct
	private void init() { // NOPMD
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new LineBorder(Color.BLUE));

		this.add(this.createAddressPanel("From"));
		this.add(this.createAddressPanel("To"));

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			this.typeOptionBox.setSelectedItem("To");
			actionPanel.add(this.typeOptionBox);

			JButton addButton = new JButton("Add");
			addButton.addActionListener(this.addEvent);
			actionPanel.add(addButton);
		}
		this.add(actionPanel);
	}

	private JPanel createAddressPanel(String type) {
		JPanel wrapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		{
			AddressPanel addressPanel = new AddressPanel(type, true);
			this.addressPanels.add(addressPanel);
			wrapPanel.add(addressPanel);

			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(this.removeEvent);
			wrapPanel.add(removeButton);
		}
		return wrapPanel;
	}

	private final ActionListener addEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			String type = (String) EditAddressPanel.this.typeOptionBox.getSelectedItem();
			JPanel addressPanel = EditAddressPanel.this.createAddressPanel(type);
			EditAddressPanel.this.add(addressPanel, EditAddressPanel.this.getComponentCount() - 1);
			EditAddressPanel.this.updateUI();
		}
	};

	private final ActionListener removeEvent = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			EditAddressPanel.this.addressPanels.remove(source.getParent());
			EditAddressPanel.this.remove(source.getParent());
			EditAddressPanel.this.updateUI();
		}
	};

	public List<AddressData> getAddressDatas() {
		List<AddressData> addressDatas = new ArrayList<AddressData>();
		for (AddressPanel addressPanel : this.addressPanels) {
			addressDatas.add(addressPanel.getAddressData());
		}
		return addressDatas;
	}
}
