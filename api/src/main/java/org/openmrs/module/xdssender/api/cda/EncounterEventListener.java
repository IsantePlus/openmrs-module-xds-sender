package org.openmrs.module.xdssender.api.cda;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.event.EventListener;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.errorhandling.ErrorHandlingService;
import org.openmrs.module.xdssender.api.errorhandling.ExportProvideAndRegisterParameters;
import org.openmrs.module.xdssender.api.errorhandling.XdsBErrorHandlingService;
import org.openmrs.module.xdssender.api.patient.PatientEcidUpdater;
import org.openmrs.module.xdssender.api.service.XdsExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("xdssender.EncounterEventListener")
public class EncounterEventListener implements EventListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EncounterEventListener.class);
	
	private static final String WILDCARD_MATCH = "ALL";
	
	private static final String PROP_ENCOUNTER_TYPE_TO_PROCESS = "xdssender.encounterTypesToProcess";

	// N.B. The following three constants are added to restrict XDSSender to only sending lab orders
	private static final int TESTS_ORDERED_CONCEPT_ID = 1271;
	private static volatile int VIRAL_LOAD_CONCEPT_ID = -1;
	private static volatile int EARLY_INFANT_DIAGNOSIS_CONCEPT_ID = -1;
	
	@Autowired
	private XdsSenderConfig config;

//	@Autowired
//	private PatientEcidUpdater ecidUpdater;
	
	@Override
	public void onMessage(Message message) {
		try {
			MapMessage mapMessage = (MapMessage) message;
			String messageAction = mapMessage.getString("action");
			
			Context.openSession();
			try {
				Context.authenticate(config.getOpenmrsUsername(), config.getOpenmrsPassword());

				if (Event.Action.CREATED.toString().equals(messageAction)) {
					LOGGER.debug("Encounter event detected");
					String uuid = ((MapMessage) message).getString("uuid");
					List<String> encounterTypesToProcess = null;
					String propEncounterTypesToProcess = Context.getAdministrationService()
							.getGlobalProperty(PROP_ENCOUNTER_TYPE_TO_PROCESS);

					if (propEncounterTypesToProcess != null) {
						LOGGER.debug("Encounter types to filter detected");

						encounterTypesToProcess = Arrays.asList(propEncounterTypesToProcess.split(","));
						if (!encounterTypesToProcess.isEmpty()) {
							if (encounterTypesToProcess.contains(WILDCARD_MATCH)) {
								LOGGER.debug("Sending for all encounter types");
								exportEncounter(uuid);

							} else {
								LOGGER.debug("Found {} encounter types to filter from global config",
										encounterTypesToProcess.size());
								LOGGER.debug("Matching encounter types to send XDS repository: {}", uuid);
								Encounter e = Context.getEncounterService().getEncounterByUuid(uuid);

								// Since we are no longer using the XDSSender to send everything to an XDS Repository,
								// we want to check that this encounter has an appropriate "order". Note that "orders"
								// are stored as obs
								boolean shouldSendEncounter = shouldSendEncounter(e);

								if (shouldSendEncounter) {
									for (String encounterTypeUuid : encounterTypesToProcess) {
										if (encounterTypeUuid.equals(e.getEncounterType().getUuid())) {
											LOGGER.debug("Exporting encounter {} to XDS repository", uuid);
											exportEncounter(uuid);
										}
									}
								} else {
									LOGGER.info("Skipping encounter {} because there are no appropriate lab orders", uuid);
								}
							}
						} else {
							LOGGER.debug("No Encounter types filter detected");
							exportEncounter(uuid);
						}
					}

				}
			} finally {
				Context.closeSession();
			}
		}
		catch (JMSException e) {
			System.out.println("Some error occurred: " + e.getErrorCode());
		}
	}

	private void exportEncounter(String encounterUuid) {
		Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
		if (encounter.getForm() == null) {
			LOGGER.warn("Skipped sending Encounter %s (formId is NULL " + "-> probably it's the creating encounter)");
		} else {
			Patient patient = Context.getPatientService().getPatient(encounter.getPatient().getPatientId());

			// TODO: Replace this with a method that queries OpenCR and fetches/updates the patient data
			// ecidUpdater.fetchEcidIfRequired(patient);
			
			XdsExportService service = Context.getService(XdsExportService.class);
			
			try {
				service.exportProvideAndRegister(encounter, patient);
			}
			catch (Exception e) {
				ErrorHandlingService errorHandler = config.getXdsBErrorHandlingService();
				if (errorHandler != null) {
					LOGGER.error("XDS export exception occurred", e);
					errorHandler.handle(prepareParameters(encounter, patient),
					    XdsBErrorHandlingService.EXPORT_PROVIDE_AND_REGISTER_DESTINATION, true,
					    ExceptionUtils.getFullStackTrace(e));
				} else {
					throw new RuntimeException("XDS export exception occurred " + "with not configured XDS.b error handler",
					        e);
				}
			}
		}
	}
	
	private String prepareParameters(Encounter encounter, Patient patient) {
		ExportProvideAndRegisterParameters parameters = new ExportProvideAndRegisterParameters(patient.getUuid(),
		        encounter.getUuid());
		try {
			return new ObjectMapper().writeValueAsString(parameters);
		}
		catch (IOException e) {
			throw new RuntimeException("Cannot prepare parameters for OutgoingMessageException", e);
		}
	}

	private boolean shouldSendEncounter(Encounter e) {
		boolean shouldSendEncounter = false;
		if (getViralLoadConceptId() != -1 && getEarlyInfantDiagnosisConceptId() != -1) {
			for (Obs obs : e.getObs()) {
				if (obs.getConcept() != null &&
						obs.getConcept().getConceptId() == TESTS_ORDERED_CONCEPT_ID &&
						obs.getValueCoded() != null && (
						obs.getValueCoded().getConceptId() == getViralLoadConceptId() ||
								obs.getValueCoded().getConceptId() == getEarlyInfantDiagnosisConceptId()
				)) {
					shouldSendEncounter = true;
					break;
				}
			}
		}
		return shouldSendEncounter;
	}

	private int getViralLoadConceptId() {
		if (VIRAL_LOAD_CONCEPT_ID == -1) {
			synchronized (EncounterEventListener.class) {
				if (VIRAL_LOAD_CONCEPT_ID == -1) {
					ConceptService conceptService = Context.getService(ConceptService.class);
					Concept concept = conceptService.getConceptByMapping("856", "CIEL");
					if (concept == null) {
						concept = conceptService.getConceptByMapping("25836-8", "LOINC");
					}

					if (concept != null) {
						VIRAL_LOAD_CONCEPT_ID = concept.getConceptId();
					}
				}
			}
		}

		return VIRAL_LOAD_CONCEPT_ID;
	}

	private int getEarlyInfantDiagnosisConceptId() {
		if (EARLY_INFANT_DIAGNOSIS_CONCEPT_ID == -1) {
			synchronized (EncounterEventListener.class) {
				if (EARLY_INFANT_DIAGNOSIS_CONCEPT_ID == -1) {
					ConceptService conceptService = Context.getService(ConceptService.class);
					Concept concept = conceptService.getConceptByMapping("844", "CIEL");
					if (concept == null) {
						concept = conceptService.getConceptByMapping("44871-2", "LOINC");
					}

					if (concept != null) {
						EARLY_INFANT_DIAGNOSIS_CONCEPT_ID = concept.getConceptId();
					}
				}
			}
		}

		return EARLY_INFANT_DIAGNOSIS_CONCEPT_ID;
	}
}
