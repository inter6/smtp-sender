package com.inter6.mail.gui.tab;

import com.inter6.mail.module.TabComponentManager;

import javax.swing.*;

public class TabComponentPanel extends JPanel implements TabComponent {

    protected String tabName;

    protected TabComponentManager tabComponentManager = TabComponentManager.getInstance();

    public TabComponentPanel(String tabName) {
        this.tabName = tabName;
    }
}
