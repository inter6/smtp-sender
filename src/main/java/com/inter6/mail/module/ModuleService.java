package com.inter6.mail.module;

import lombok.Setter;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class ModuleService {

	@Setter
	private static ApplicationContext context;

	public static Object getBean(String name) {
		return context.getBean(name);
	}

	public static <T> T getBean(Class<T> type) {
		return context.getBean(type);
	}

	public static <T> T getBean(String name, Class<T> type) {
		return context.getBean(name, type);
	}

	public static <T> Map<String, T> getBeans(Class<T> type) {
		return context.getBeansOfType(type);
	}

	public static ApplicationContext getContext() {
		return context;
	}
}