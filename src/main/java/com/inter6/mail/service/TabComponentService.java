package com.inter6.mail.service;

import com.inter6.mail.gui.RootTabPanel;
import com.inter6.mail.gui.TabComponent;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TabComponentService {

	@Autowired
	private RootTabPanel rootTabPanel;

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
}
