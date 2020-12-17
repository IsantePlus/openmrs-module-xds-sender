package org.openmrs.module.xdssender.api.service.impl;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.hl7.fhir.r4.model.Bundle;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.service.XdsImportService;
import org.openmrs.module.xdssender.api.xds.ShrRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("xdsSender.ShrImportService")
public class ShrImportServiceImpl implements XdsImportService {

	private static final Log LOGGER = LogFactory.getLog(ShrImportServiceImpl.class);

	@Autowired
	private XdsSenderConfig config;

	@Autowired
	private ShrRetriever shrRetriever;

	// Todo: fix autowiring
	// @Autowired
	// @Qualifier("fhirContext")
 	private FhirContext fhirContext;

	public FhirContext getFhirContext() {
		return fhirContext;
	}

	public void setFhirContext(FhirContext fhirContext) {
		this.fhirContext = fhirContext;
	}

	@Override
	public Ccd retrieveCCD(Patient patient) throws XDSException {
		Ccd ccd = null;
		Bundle result;
		try {
			shrRetriever.setFhirContext(this.getFhirContext());
			result = shrRetriever.sendRetrieveCCD(patient);
		} catch (Exception e) {
			LOGGER.error("Unable to load CCD content", e);
			return null;
		}

		if(result != null && result.hasTotal() && result.getTotal() > 0) {
			ccd = new Ccd();
			ccd.setPatient(patient);
			ccd.setDocument(fhirContext.newJsonParser().encodeResourceToString(result));
		}

		return ccd;
	}
}
