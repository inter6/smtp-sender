package com.inter6.mail.gui.data.edit;

import com.inter6.mail.gui.tab.TabComponentPanel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EditDkimPanel extends TabComponentPanel {
    private static final long serialVersionUID = -2264693851880984890L;

    public EditDkimPanel(String tabName) {
        super(tabName);
    }

    @PostConstruct
    private void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new LineBorder(Color.YELLOW));

        JPanel wrapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        {
            JCheckBox useCheckBox = new JCheckBox("DKIM-Signature");
            useCheckBox.setEnabled(false); // TODO: remove, if implemented
            wrapPanel.add(useCheckBox);

            JPanel signerPanel = new JPanel();
            signerPanel.setLayout(new BoxLayout(signerPanel, BoxLayout.Y_AXIS));
            {
                JPanel domainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                {
                    domainPanel.add(new JLabel("Domain"));
                    domainPanel.add(new JTextField(20));
                    domainPanel.add(new JLabel("Selector"));
                    domainPanel.add(new JTextField(13));
                }
                signerPanel.add(domainPanel);

                JPanel certPanel = new JPanel(new BorderLayout());
                {
                    certPanel.add(new JLabel("Private Key"), BorderLayout.NORTH);

                    JTextArea privateKeyArea = new JTextArea(5, 23);
                    privateKeyArea.setText("TODO: not yet implemented"); // TODO: remove, if implemented
                    certPanel.add(new JScrollPane(privateKeyArea), BorderLayout.CENTER);
                }
                signerPanel.add(certPanel);
            }
            wrapPanel.add(signerPanel);

            wrapPanel.add(new JButton("Verify"));
        }
        this.add(wrapPanel);
    }
}
