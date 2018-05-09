package org.openmrs.module.xdssender.api.service;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.api.domain.Ccd;

import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public interface CcdService {
	
	Ccd getLocallyStoredCcd(Patient patient);
	
	Ccd downloadAndSaveCcd(Patient patient) throws XDSException, IOException, KeyStoreException, NoSuchAlgorithmException,
	        KeyManagementException;
	
	void downloadCcdAsPDF(OutputStream stream, Patient patient);
}
