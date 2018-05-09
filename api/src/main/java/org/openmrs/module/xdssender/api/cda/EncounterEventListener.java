package org.openmrs.module.xdssender.api.cda;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.service.XdsExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

@Component("xdssender.EncounterEventListener")
public class EncounterEventListener implements EventListener {
	
	@Autowired
	private XdsSenderConfig config;
	
	@Override
	public void onMessage(Message message) {
		try {
			MapMessage mapMessage = (MapMessage) message;
			String messageAction = mapMessage.getString("action");
			
			Context.openSession();
			Context.authenticate(config.getOpenmrsUsername(), config.getOpenmrsPassword());
			
			if (Event.Action.CREATED.toString().equals(messageAction)
			        || Event.Action.UPDATED.toString().equals(messageAction)) {
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
