package org.openmrs.module.xdssender.api.notificationspullpoint;

import ca.uhn.hl7v2.model.Message;

import java.util.List;

public interface NotificationsPullPointClient {
	
	List<Message> getNewMessages();
}
