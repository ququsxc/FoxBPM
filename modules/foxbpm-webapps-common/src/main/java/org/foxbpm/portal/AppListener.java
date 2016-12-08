package org.foxbpm.portal;

import org.foxbpm.portal.service.ExpenseConsumer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class AppListener implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent e) {
		// new ExpenseConsumer("top2").start();
	}

}
