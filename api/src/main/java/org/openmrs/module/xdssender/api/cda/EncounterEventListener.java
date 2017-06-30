package org.openmrs.module.xdssender.api.cda;

import org.openmrs.event.Event;
import org.openmrs.event.EventListener;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

public class EncounterEventListener implements EventListener {
	
	@Override
	public void onMessage(Message message) {
		try {
			MapMessage mapMessage = (MapMessage) message;
			if (Event.Action.CREATED.toString().equals(mapMessage.getString("action"))) {
				String uuid = ((MapMessage) message).getString("uuid");
			}
		}
		catch (JMSException e) {
			System.out.println("Some error occurred" + e.getErrorCode());
		}
	}
	
}
