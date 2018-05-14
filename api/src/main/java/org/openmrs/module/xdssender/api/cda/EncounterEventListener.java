package org.openmrs.module.xdssender.api.cda;

import java.io.IOException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.errorhandling.ErrorHandlingService;
import org.openmrs.module.xdssender.api.errorhandling.ExportProvideAndRegisterParameters;
import org.openmrs.module.xdssender.api.errorhandling.XdsBErrorHandlingService;
import org.openmrs.module.xdssender.api.service.XdsExportService;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("xdssender.EncounterEventListener")
public class EncounterEventListener implements EventListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EncounterEventListener.class);

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
				if (encounter.getForm() == null) {
					LOGGER.warn("Skipped sending Encounter %s (formId is NULL "
							+ "-> probably it's the creating encounter)");
				} else {
					Patient patient = Context.getPatientService()
							.getPatient(encounter.getPatient().getPatientId());
					XdsExportService service = Context.getService(XdsExportService.class);
					try {
						service.exportProvideAndRegister(encounter, patient);
					} catch (Exception e) {
						ErrorHandlingService errorHandler = config.getXdsBErrorHandlingService();
						if (errorHandler != null) {
							LOGGER.error("XDS export exception occurred", e);
							errorHandler.handle(
									prepareParameters(encounter, patient),
									XdsBErrorHandlingService.EXPORT_PROVIDE_AND_REGISTER_DESTINATION,
									true,
									ExceptionUtils.getFullStackTrace(e));
						} else {
							throw new RuntimeException("XDS export exception occurred "
									+ "with not configured XDS.b error handler", e);
						}
					}
				}
			}
			
			Context.closeSession();
		}
		catch (JMSException e) {
			System.out.println("Some error occurred" + e.getErrorCode());
		}
	}
	
	private String prepareParameters(Encounter encounter, Patient patient) {
		ExportProvideAndRegisterParameters parameters =
				new ExportProvideAndRegisterParameters(patient.getUuid(), encounter.getUuid());
		try {
			return new ObjectMapper().writeValueAsString(parameters);
		} catch (IOException e) {
			throw new RuntimeException("Cannot prepare parameters for OutgoingMessageException", e);
		}
	}
}
