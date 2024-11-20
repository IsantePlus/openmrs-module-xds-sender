package org.openmrs.module.xdssender.api.scheduler;

import ca.uhn.hl7v2.model.Message;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7Service;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.notificationspullpoint.NotificationsPullPointClient;
import org.openmrs.module.xdssender.api.notificationspullpoint.impl.NotificationsPullPointClientImpl;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class PullNotificationsTask extends AbstractTask {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PullNotificationsTask.class);
	
	public static final String TASK_NAME = "XDS Sender Pull Notifications Task";
	
	public static final String TASK_DESCRIPTION = "XDS Sender task for pulling notifications from Pull Point";
	
	public static final String DEFAULT_INTERVAL_SECONDS = "3600";
	
	@Override
	public void execute() {
		LOGGER.info("Executing " + TASK_NAME);
		HL7Service hl7Service = Context.getHL7Service();
		boolean success = false;
		for (Message msg : getNotificationsPullPointClient().getNewMessages()) {
			try {
				hl7Service.processHL7Message(msg);
				success = true;
			}
			catch (Exception e) {
				success = false;
				LOGGER.error(e.getMessage(), e);
			}
		}

		if(success){
			Context.getAdministrationService().setGlobalProperty(XdsSenderConstants.PULL_NOTIFICATIONS_TASK_LAST_SUCCESS_RUN ,DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
		}
		
	}
	
	private NotificationsPullPointClient getNotificationsPullPointClient() {
		return Context.getRegisteredComponent("xdssender.NotificationsPullPointClientImpl",
		    NotificationsPullPointClientImpl.class);
	}
}
