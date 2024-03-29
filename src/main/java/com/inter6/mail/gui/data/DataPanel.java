package com.inter6.mail.gui.data;

import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.tab.TabComponentPanel;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.module.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataPanel extends TabComponentPanel implements ConfigObserver {
    private static final long serialVersionUID = 1398719678774376633L;

    @Autowired
    private AppConfig appConfig;

    private final JRadioButton editSourceButton = new JRadioButton("Editor");
    private final JRadioButton mimeSourceButton = new JRadioButton("MIME");
    private final JRadioButton emlSourceButton = new JRadioButton("EML");
    private final JPanel sourceInputPanel = new JPanel(new BorderLayout());
    private SendJobBuilder selectedJobBuilder;

    public DataPanel(String tabName) {
        super(tabName);
    }

    @PostConstruct
    private void init() {
        tabComponentManager.getTabComponent(tabName, EditSourcePanel.class);
        tabComponentManager.getTabComponent(tabName, MimeSourcePanel.class);
        tabComponentManager.getTabComponent(tabName, EmlSourcePanel.class);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(tabComponentManager.getTabComponent(tabName, EnvelopePanel.class));

        JPanel sourcePanel = new JPanel(new BorderLayout());
        {
            JPanel sourceSelectPanel = new JPanel(new FlowLayout());
            {
                ActionListener sourceSelectChangeEvent = createSourceSelectChangeEvent();
                this.editSourceButton.addActionListener(sourceSelectChangeEvent);
                this.mimeSourceButton.addActionListener(sourceSelectChangeEvent);
                this.emlSourceButton.addActionListener(sourceSelectChangeEvent);
                sourceSelectPanel.add(this.editSourceButton);
                sourceSelectPanel.add(this.mimeSourceButton);
                sourceSelectPanel.add(this.emlSourceButton);

                ButtonGroup sourceSelectGroup = new ButtonGroup();
                sourceSelectGroup.add(this.editSourceButton);
                sourceSelectGroup.add(this.mimeSourceButton);
                sourceSelectGroup.add(this.emlSourceButton);

                this.editSourceButton.doClick();
            }
            sourcePanel.add(sourceSelectPanel, BorderLayout.NORTH);
            sourcePanel.add(this.sourceInputPanel, BorderLayout.CENTER);
        }
        this.add(sourcePanel);
    }

    private ActionListener createSourceSelectChangeEvent() {
        return event -> {
            Object sourceButton = event.getSource();
            if (sourceButton == DataPanel.this.editSourceButton) {
                DataPanel.this.setSourcePanel(tabComponentManager.getTabComponent(tabName, EditSourcePanel.class));
            } else if (sourceButton == DataPanel.this.mimeSourceButton) {
                DataPanel.this.setSourcePanel(tabComponentManager.getTabComponent(tabName, MimeSourcePanel.class));
            } else if (sourceButton == DataPanel.this.emlSourceButton) {
                DataPanel.this.setSourcePanel(tabComponentManager.getTabComponent(tabName, EmlSourcePanel.class));
            }
        };
    }

    private void setSourcePanel(JPanel sourcePanel) {
        this.selectedJobBuilder = (SendJobBuilder) sourcePanel;
        this.sourceInputPanel.removeAll();
        this.sourceInputPanel.add(sourcePanel);
        this.sourceInputPanel.updateUI();
    }

    public AbstractSmtpSendJob getSendJob() throws Throwable {
        return this.selectedJobBuilder.buildSendJob();
    }

    @Override
    public void loadConfig() {
        String sourcePanel = this.appConfig.getString(tabName + ".source.type");
        if ("edit".equals(sourcePanel)) {
            this.editSourceButton.doClick();
        } else if ("mime".equals(sourcePanel)) {
            this.mimeSourceButton.doClick();
        } else if ("eml".equals(sourcePanel)) {
            this.emlSourceButton.doClick();
        } else {
            this.editSourceButton.doClick();
        }
    }

    @Override
    public void updateConfig() {
        String selectType;
        if (this.editSourceButton.isSelected()) {
            selectType = "edit";
        } else if (this.mimeSourceButton.isSelected()) {
            selectType = "mime";
        } else if (this.emlSourceButton.isSelected()) {
            selectType = "eml";
        } else {
            selectType = "edit";
        }
        this.appConfig.setProperty(tabName + ".source.type", selectType);
    }
}
