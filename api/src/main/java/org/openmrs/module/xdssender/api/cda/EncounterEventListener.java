package org.openmrs.module.xdssender.api.cda;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.xdssender.api.service.XdsExportSerivce;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

public class EncounterEventListener implements EventListener {

	@Autowired
	private XdsExportSerivce exportSerivce;
	
	@Override
	public void onMessage(Message message) {
		try {
			MapMessage mapMessage = (MapMessage) message;
			if (Event.Action.CREATED.toString().equals(mapMessage.getString("action"))) {
				String uuid = ((MapMessage) message).getString("uuid");
				Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
				exportSerivce.exportProvideAndRegister(encounter, encounter.getPatient());
			}
		}
		catch (JMSException e) {
			System.out.println("Some error occurred" + e.getErrorCode());
		}
	}
	
}
