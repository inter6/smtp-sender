package com.inter6.mail;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.module.ModuleService;
import com.inter6.mail.service.XTrustProvider;

@Slf4j
public class Application {

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
