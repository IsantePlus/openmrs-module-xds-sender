package org.openmrs.module.xdssender.api.service;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;

public interface CcdService {
	
	@Transactional(readOnly = true)
	Ccd getLocallyStoredCcd(Patient patient);
	
	@Transactional
	Ccd downloadAndSaveCcd(Patient patient) throws XDSException;
	
	@Transactional(readOnly = true)
	void downloadCcdAsPDF(OutputStream stream, Patient patient);
}
