package org.openmrs.module.xdssender.api.cda;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.xdssender.api.service.XdsExportService;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

public class EncounterEventListener implements EventListener {
	
	@Override
	public void onMessage(Message message) {
		try {
			MapMessage mapMessage = (MapMessage) message;
			Context.openSession();
			Context.authenticate("admin", "Admin123");
			if (Event.Action.CREATED.toString().equals(mapMessage.getString("action"))) {
				String uuid = ((MapMessage) message).getString("uuid");
				Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
				Patient patient = Context.getPatientService().getPatient(encounter.getPatient().getPatientId());
				XdsExportService serivce = Context.getService(XdsExportService.class);
				serivce.exportProvideAndRegister(encounter, patient);
			}
			Context.closeSession();
		}
		catch (JMSException e) {
			System.out.println("Some error occurred" + e.getErrorCode());
		}
	}
	
}
