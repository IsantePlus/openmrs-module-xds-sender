package org.openmrs.module.xdssender.api.scheduler;

import ca.uhn.hl7v2.model.Message;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7Service;
import org.openmrs.module.xdssender.api.notificationspullpoint.NotificationsPullPointClient;
import org.openmrs.module.xdssender.api.notificationspullpoint.impl.NotificationsPullPointClientImpl;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PullNotificationsTask extends AbstractTask {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PullNotificationsTask.class);
	
	public static final String TASK_NAME = "XDS Sender Pull Notifications Task";
	
	public static final String TASK_DESCRIPTION = "XDS Sender task for pulling notifications from Pull Point";
	
	public static final String DEFAULT_INTERVAL_SECONDS = "3600";
	
	@Override
	public void execute() {
		LOGGER.info("Executing " + TASK_NAME);
		HL7Service hl7Service = Context.getHL7Service();
		for (Message msg : getNotificationsPullPointClient().getNewMessages()) {
			try {
				hl7Service.processHL7Message(msg);
			}
			catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	private NotificationsPullPointClient getNotificationsPullPointClient() {
		return Context.getRegisteredComponent("xdssender.NotificationsPullPointClientImpl",
		    NotificationsPullPointClientImpl.class);
	}
}
