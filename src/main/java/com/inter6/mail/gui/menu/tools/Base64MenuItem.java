package com.inter6.mail.gui.menu.tools;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.module.ModuleService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class Base64MenuItem extends JMenuItem implements ActionListener {
    private static final long serialVersionUID = 3869102798552221510L;

    private Base64Dialog base64Dialog;

    @PostConstruct
    private void init() {
        this.setText("Base64 Encoder/Decoder");
        this.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.base64Dialog == null || !this.base64Dialog.isVisible()) {
            this.base64Dialog = new Base64Dialog();
        }
        this.base64Dialog.setVisible(true);
    }

    private static class Base64Dialog extends JDialog {
        private static final long serialVersionUID = -6824166515619216301L;

        private final JTextArea decodeTextArea = new JTextArea(9, 30);
        private final JTextArea encodeTextArea = new JTextArea(9, 30);
        private final JTextField charsetField = new JTextField("UTF-8", 8);

        private Base64Dialog() {
            super(ModuleService.getBean(MainFrame.class));
            this.setTitle("Base64 Encoder/Decoder");
            this.setSize(600, 400);
            this.setResizable(false);
            this.initLayout();
        }

        private void initLayout() {
            this.setLayout(new BorderLayout());
            this.add(new JScrollPane(this.decodeTextArea), BorderLayout.NORTH);
            JPanel actionPanel = new JPanel(new FlowLayout());
            {
                JButton encodeButton = new JButton("Encode ▼");
                encodeButton.addActionListener(this.createEncodeAction());
                actionPanel.add(encodeButton);

                JButton decodeButton = new JButton("Decode ▲");
                decodeButton.addActionListener(this.createDecodeAction());
                actionPanel.add(decodeButton);

                actionPanel.add(this.charsetField);
            }
            this.add(actionPanel, BorderLayout.CENTER);
            this.add(new JScrollPane(this.encodeTextArea), BorderLayout.SOUTH);
        }

        private ActionListener createEncodeAction() {
            return new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        String decodeText = StringUtils.defaultIfEmpty(Base64Dialog.this.decodeTextArea.getText(), "");
                        String charset = StringUtils.defaultIfBlank(Base64Dialog.this.charsetField.getText(), "UTF-8");
                        String encodeText = Base64.encodeBase64String(decodeText.getBytes(charset));
                        Base64Dialog.this.encodeTextArea.setText(encodeText);
                    } catch (Throwable e) {
                        JOptionPane.showMessageDialog(Base64Dialog.this, e.getClass().getSimpleName() + " - " + e.getMessage(), "Encoding fail !", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
        }

        private ActionListener createDecodeAction() {
            return new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    try {
                        String encodeText = StringUtils.defaultIfEmpty(Base64Dialog.this.encodeTextArea.getText(), "");
                        String charset = StringUtils.defaultIfBlank(Base64Dialog.this.charsetField.getText(), "UTF-8");
                        byte[] decodeBytes = Base64.decodeBase64(encodeText);
                        Base64Dialog.this.decodeTextArea.setText(new String(decodeBytes, charset));
                    } catch (Throwable e) {
                        JOptionPane.showMessageDialog(Base64Dialog.this, e.getClass().getSimpleName() + " - " + e.getMessage(), "Decoding fail !", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
        }
    }
}
