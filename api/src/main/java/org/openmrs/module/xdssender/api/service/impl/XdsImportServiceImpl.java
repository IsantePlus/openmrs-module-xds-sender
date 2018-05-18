package org.openmrs.module.xdssender.api.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientService;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.service.XdsImportService;
import org.openmrs.module.xdssender.api.xds.XdsRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Component("xdsSender.XdsImportService")
public class XdsImportServiceImpl implements XdsImportService {
	
	private static final Log LOGGER = LogFactory.getLog(XdsImportServiceImpl.class);
	
	private static final int SUCCESS_CODE = 200;
	
	private static final String ECID_NAME = "ECID";
	
	@Autowired
	private XdsSenderConfig config;
	
	@Autowired
	private XdsRetriever xdsRetriever;
	
	@Autowired
	private PatientService patientService;
	
	@Override
	public Ccd retrieveCCD(Patient patient) throws XDSException {
		Ccd ccd;
		
		String patientEcid = extractPatientEcid(patient);
		try {
			HttpResponse response = xdsRetriever.sendRetrieveCCD(patientEcid);
			validateResponse(response);
			
			ccd = new Ccd();
			ccd.setDocument(IOUtils.toString(response.getEntity().getContent()));
			ccd.setPatient(patient);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		catch (KeyStoreException e) {
			throw new RuntimeException(e);
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
		
		return ccd;
	}
	
	private void validateResponse(HttpResponse response) throws HttpResponseException {
		if (response.getStatusLine().getStatusCode() != SUCCESS_CODE) {
			throw new HttpResponseException(response.getStatusLine().getStatusCode(), "Unable to import a CCD document");
		}
	}
	
	private String extractPatientEcid(Patient patient) {
		String patientEcid = null;
		for (PatientIdentifier patientIdentifier : patient.getIdentifiers()) {
			if (patientIdentifier.getIdentifierType().getName().equals(ECID_NAME)) {
				patientEcid = patientIdentifier.getIdentifier();
			}
		}
		return patientEcid;
	}
}
