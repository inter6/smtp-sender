package com.inter6.mail.gui.menu.tools;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.module.ModuleService;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;

@Component
public class DnsMenuItem extends JMenuItem implements ActionListener {
	private static final long serialVersionUID = 3869102798552221510L;

	private DnsDialog dnsDialog;

	@PostConstruct
	private void init() { // NOPMD
		this.setText("DNS Query");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.dnsDialog == null || !this.dnsDialog.isVisible()) {
			this.dnsDialog = new DnsDialog();
		}
		this.dnsDialog.setVisible(true);
	}

	private static class DnsDialog extends JDialog {
		private static final long serialVersionUID = -8148265075267497887L;

		private final JTextField searchHostField = new JTextField(30);
		private final JCheckBox serverUseCheckBox = new JCheckBox();
		private final JTextField serverIpField = new JTextField("8.8.8.8", 20);
		private final JTextField serverPortField = new JTextField("53", 5);
		private final JTextArea resultArea = new JTextArea(9, 30);

		private DnsDialog() {
			super(ModuleService.getBean(MainFrame.class));
			this.setTitle("DNS Query");
			this.setSize(600, 300);
			this.setResizable(false);
			this.initLayout();
		}

		private void initLayout() {
			this.setLayout(new BorderLayout());

			JPanel wrapPanel = new JPanel();
			wrapPanel.setLayout(new BoxLayout(wrapPanel, BoxLayout.Y_AXIS));
			{
				JPanel targetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				{
					targetPanel.add(new JLabel("Search Domain: "));
					targetPanel.add(this.searchHostField);
					JButton queryButton = new JButton("Query");
					queryButton.addActionListener(this.createQueryEvent());
					targetPanel.add(queryButton);
				}
				wrapPanel.add(targetPanel);

				JPanel serverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				{
					serverPanel.add(this.serverUseCheckBox);
					serverPanel.add(new JLabel("DNS Server: "));
					serverPanel.add(this.serverIpField);
					serverPanel.add(this.serverPortField);
				}
				wrapPanel.add(serverPanel);

				this.resultArea.setEditable(false);
				wrapPanel.add(new JScrollPane(this.resultArea));
			}
			this.add(wrapPanel, BorderLayout.CENTER);
		}

		private ActionListener createQueryEvent() {
			return new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					try {
						String searchHost = DnsDialog.this.searchHostField.getText();
						SimpleResolver resolver = new SimpleResolver();
						if (DnsDialog.this.serverUseCheckBox.isSelected()) {
							resolver.setAddress(new InetSocketAddress(
									DnsDialog.this.serverIpField.getText(),
									Integer.parseInt(DnsDialog.this.serverPortField.getText())));
						}
						this.queryDns(searchHost, "A", resolver);
						this.queryDns(searchHost, "MX", resolver);
					} catch (Throwable e) {
						JOptionPane.showMessageDialog(DnsDialog.this,
								e.getClass().getSimpleName() + " - " + e.getMessage(),
								"DNS Query fail !",
								JOptionPane.ERROR_MESSAGE);
					}
				}

				private void queryDns(String searchHost, String type, SimpleResolver resolver) {
					try {
						Lookup lookup = new Lookup(searchHost, org.xbill.DNS.Type.value(type));
						lookup.setResolver(resolver);
						Record[] records = lookup.run();
						if (ArrayUtils.isEmpty(records)) {
							DnsDialog.this.resultArea.append("not found " + type + " record of " + searchHost + "\n");
							return;
						}
						for (Record record : lookup.run()) {
							DnsDialog.this.resultArea.append(record.toString() + "\n");
						}
					} catch (Throwable e) {
						JOptionPane.showMessageDialog(DnsDialog.this,
								e.getClass().getSimpleName() + " - " + e.getMessage(),
								"DNS Query fail !",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};
		}
	}
}
