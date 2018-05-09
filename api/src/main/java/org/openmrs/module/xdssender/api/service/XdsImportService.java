package org.openmrs.module.xdssender.api.service;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.api.domain.Ccd;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public interface XdsImportService {
	
	Ccd retrieveCCD(Patient patient) throws XDSException, IOException, KeyStoreException, NoSuchAlgorithmException,
	        KeyManagementException;
}
