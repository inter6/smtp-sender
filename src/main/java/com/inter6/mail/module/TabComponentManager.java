package com.inter6.mail.module;

import com.inter6.mail.gui.tab.RootTabPanel;
import com.inter6.mail.gui.tab.TabComponent;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.ArrayList;
import java.util.List;

public class TabComponentManager {

    private MultiKeyMap tabComponents = new MultiKeyMap();

    public <T extends TabComponent> T getTabComponent(String tabName, Class<T> type) {
        Object component = tabComponents.get(tabName, type.getName());
        if (component == null) {
            component = ModuleService.getBean(type, tabName);
            tabComponents.put(tabName, type.getName(), component);
        }
        return (T) component;
    }

    public <T extends TabComponent> T getActiveTabComponent(Class<T> type) {
        RootTabPanel rootTabPanel = ModuleService.getBean(RootTabPanel.class);
        return getTabComponent(rootTabPanel.getActiveTabName(), type);
    }

    public <T> List<T> getTabComponents(Class<T> type) {
        List<T> components = new ArrayList<>();
        for (Object component : tabComponents.values()) {
            if (type.isInstance(component)) {
                components.add((T) component);
            }
        }
        return components;
    }

    public static TabComponentManager getInstance() {
        return HOLDER._instance;
    }

    private static class HOLDER {
        private static TabComponentManager _instance = new TabComponentManager();
    }
}
