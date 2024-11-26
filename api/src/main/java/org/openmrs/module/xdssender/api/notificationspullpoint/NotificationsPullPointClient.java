package org.openmrs.module.xdssender.api.notificationspullpoint;

import java.util.List;

import org.openmrs.Location;

import ca.uhn.hl7v2.model.Message;

public interface NotificationsPullPointClient {
	
	boolean getNewMessages();

	boolean getNewMessages(Location currentLocation);
}
