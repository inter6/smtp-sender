package com.inter6.mail;

import com.inter6.mail.gui.MainFrame;
import com.inter6.mail.service.ModuleService;
import com.inter6.mail.service.XTrustProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class Application {

	public static void main(String[] args) {
		log.info("application start");
		ConfigurableApplicationContext context = null;
		try {
			context = new SpringApplicationBuilder(Application.class)
					.registerShutdownHook(true)
					.headless(false)
					.showBanner(false)
					.run(args);
			log.debug("load beans list - " + ArrayUtils.toString(context.getBeanDefinitionNames()));

			XTrustProvider.install();

			MainFrame mainFrame = ModuleService.getBean(MainFrame.class);
			mainFrame.execute();
			do {
				Thread.sleep(1000);
			} while (mainFrame.isVisible());
		} catch (Throwable e) {
			log.error("occurred application kill !", e);
		} finally {
			log.info("application exit");
			if (context != null) {
				context.close();
			}
		}
	}

	@Autowired
	private void init(ApplicationContext context) {
		ModuleService.setContext(context);
	}
}
