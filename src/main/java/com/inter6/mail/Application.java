package com.inter6.mail;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.module.ModuleService;
import com.inter6.mail.service.XTrustProvider;

public class Application {
	private final static Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		log.info("application start");

		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("app-context.xml");
			context.registerShutdownHook();
			log.debug("load beans list - " + ArrayUtils.toString(context.getBeanDefinitionNames()));

			XTrustProvider.install();

			MainFrame mainFrame = ModuleService.getBean(MainFrame.class);
			mainFrame.execute();
			do {
				Thread.sleep(1000);
			} while (mainFrame.isVisible());
		} catch (Throwable e) {
			log.error("occured application kill !", e);
		} finally {
			log.info("application exit");
			if (context != null) {
				context.close();
			}
		}
	}
}
