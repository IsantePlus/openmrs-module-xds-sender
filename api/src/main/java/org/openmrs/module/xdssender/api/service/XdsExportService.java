package org.openmrs.module.xdssender.api.service;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.xdssender.api.model.DocumentInfo;

public interface XdsExportService extends OpenmrsService {
	
	DocumentInfo exportProvideAndRegister(Encounter encounter, Patient patient);
	
}
