package com.inter6.mail.module;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ModuleService {

	private static ApplicationContext context; // NOPMD

	@Autowired
	private ModuleService(ApplicationContext applicationContext) {
		context = applicationContext;
	}

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