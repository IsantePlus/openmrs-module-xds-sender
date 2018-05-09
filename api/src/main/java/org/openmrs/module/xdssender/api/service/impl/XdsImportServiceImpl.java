package org.openmrs.module.xdssender.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	public Ccd retrieveCCD(Patient patient) throws XDSException, IOException, KeyStoreException, NoSuchAlgorithmException,
	        KeyManagementException {
		
		String patientEcid = extractPatientEcid(patient);
		
		CloseableHttpResponse response = xdsRetriever.sendRetrieveCCD(patientEcid);
		validateResponse(response);
		
		Ccd ccd = new Ccd();
		
		ccd.setDocument(parseContent(response).toString());
		ccd.setPatient(patient);
		
		return ccd;
	}
	
	private void validateResponse(CloseableHttpResponse response) throws HttpResponseException {
		if (response.getStatusLine().getStatusCode() != SUCCESS_CODE) {
			throw new HttpResponseException(response.getStatusLine().getStatusCode(), "Unable to import a CCD document");
		}
	}
	
	private String parseContent(CloseableHttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		
		StringBuffer result = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
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
