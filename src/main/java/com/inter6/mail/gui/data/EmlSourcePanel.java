package com.inter6.mail.gui.data;

import com.google.gson.Gson;
import com.inter6.mail.gui.ConfigObserver;
import com.inter6.mail.gui.component.DatePanel;
import com.inter6.mail.gui.component.SendDelayPanel;
import com.inter6.mail.gui.tab.TabComponentPanel;
import com.inter6.mail.job.SendJobBuilder;
import com.inter6.mail.job.smtp.AbstractSmtpSendJob;
import com.inter6.mail.job.smtp.EmlSmtpSendJob;
import com.inter6.mail.model.AppSession;
import com.inter6.mail.model.component.DateData;
import com.inter6.mail.model.component.SendDelayData;
import com.inter6.mail.model.data.EmlSourceData;
import com.inter6.mail.module.AppConfig;
import com.inter6.mail.module.ModuleService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmlSourcePanel extends TabComponentPanel implements SendJobBuilder, ConfigObserver {
    private static final long serialVersionUID = -3388480705076640191L;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AppSession appSession;

    private final DefaultListModel<String> fileListModel = new DefaultListModel<>();
    private final JList<String> fileList = new JList<>(this.fileListModel);
    private final JCheckBox recursiveCheckButton = new JCheckBox("Recursive");
    private final DatePanel replaceDatePanel = new DatePanel("Replace Date", 20, false, true);
    private final SendDelayPanel sendDelayPanel = new SendDelayPanel();

    public EmlSourcePanel(String tabName) {
        super(tabName);
    }

    @PostConstruct
    private void init() {
        this.setLayout(new BorderLayout());

        this.add(fileList, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        {
            JButton addButton = new JButton("Add");
            JButton removeButton = new JButton("Remove");
            JButton dedupAndSortButton = new JButton("Dedup&Sort");
            addButton.addActionListener(this.createAddEvent());
            removeButton.addActionListener(this.createRemoveEvent());
            dedupAndSortButton.addActionListener(this.createDedupAndSortEvent());
            actionPanel.add(addButton);
            actionPanel.add(removeButton);
            actionPanel.add(dedupAndSortButton);
            actionPanel.add(this.recursiveCheckButton);
        }
        this.add(actionPanel, BorderLayout.EAST);

        JPanel subActionPanel = new JPanel();
        subActionPanel.setLayout(new BoxLayout(subActionPanel, BoxLayout.Y_AXIS));
        {
            subActionPanel.add(replaceDatePanel);
            subActionPanel.add(sendDelayPanel);
        }
        this.add(subActionPanel, BorderLayout.SOUTH);
    }

    private ActionListener createAddEvent() {
        return event -> {
            JFileChooser fileChooser = new JFileChooser(EmlSourcePanel.this.appSession.getLastSelectSourceDir());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (fileChooser.showOpenDialog(EmlSourcePanel.this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                EmlSourcePanel.this.fileListModel.addElement(file.getAbsolutePath());

                File dir = file;
                if (dir.isFile()) {
                    dir = dir.getParentFile();
                }
                EmlSourcePanel.this.appSession.setLastSelectSourceDir(dir.getAbsolutePath());
            }
        };
    }

    private ActionListener createRemoveEvent() {
        return event -> {
            for (String file : EmlSourcePanel.this.fileList.getSelectedValuesList()) {
                EmlSourcePanel.this.fileListModel.removeElement(file);
            }
        };
    }

    private ActionListener createDedupAndSortEvent() {
        return event -> {
            if (EmlSourcePanel.this.fileListModel.isEmpty()) {
                return;
            }
            Set<String> files = new TreeSet<>();
            for (int i = 0; i < EmlSourcePanel.this.fileListModel.size(); i++) {
                files.add(EmlSourcePanel.this.fileListModel.get(i));
            }
            EmlSourcePanel.this.fileListModel.clear();
            for (String file : files) {
                EmlSourcePanel.this.fileListModel.addElement(file);
            }
        };
    }

    @Override
    public AbstractSmtpSendJob buildSendJob() {
        EmlSmtpSendJob emlSmtpSendJob = ModuleService.getBean(EmlSmtpSendJob.class);
        emlSmtpSendJob.setTabName(tabName);
        emlSmtpSendJob.setEmlSourceData(this.getEmlSourceData());
        return emlSmtpSendJob;
    }

    private EmlSourceData getEmlSourceData() {
        EmlSourceData emlSourceData = new EmlSourceData();
        List<String> files = new ArrayList<>();
        for (int i = 0; i < this.fileListModel.size(); i++) {
            files.add(this.fileListModel.get(i));
        }
        emlSourceData.setFiles(files);
        emlSourceData.setRecursive(this.recursiveCheckButton.isSelected());
        emlSourceData.setReplaceDateData(this.replaceDatePanel.getDateData());
        emlSourceData.setSendDelayData(this.sendDelayPanel.getSendDelayData());
        return emlSourceData;
    }

    @Override
    public void loadConfig() {
        this.fileListModel.clear();
        EmlSourceData emlSourceData = new Gson().fromJson(this.appConfig.getString(tabName + ".eml.source.data"), EmlSourceData.class);
        if (emlSourceData == null) {
            return;
        }

        Collection<String> files = emlSourceData.getFiles();
        if (CollectionUtils.isNotEmpty(files)) {
            for (String file : files) {
                this.fileListModel.addElement(file);
            }
        }
        this.recursiveCheckButton.setSelected(emlSourceData.isRecursive());

        DateData replaceDateData = emlSourceData.getReplaceDateData();
        if (replaceDateData != null) {
            this.replaceDatePanel.setDateData(replaceDateData);
        }

        SendDelayData sendDelayData = emlSourceData.getSendDelayData();
        if (sendDelayData != null) {
            this.sendDelayPanel.setSendDelayData(sendDelayData);
        }
    }

    @Override
    public void updateConfig() {
        this.appConfig.setProperty(tabName + ".eml.source.data", new Gson().toJson(this.getEmlSourceData()));
    }
}
