package org.openmrs.module.xdssender.api.service;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.model.RequestDate;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;

public interface CcdService {
	
	@Transactional(readOnly = true)
	Ccd getLocallyStoredCcd(Patient patient);

	@Transactional
	String getHtmlParsedLocallyStoredCcd(Patient patient);

	@Transactional
	String getHtmlParsedLocallyStoredCcd(Ccd ccd);

	@Transactional
	Ccd downloadAndSaveCcd(Patient patient) throws XDSException;

	@Transactional(readOnly = true)
	void downloadCcdAsPDF(OutputStream stream, Patient patient);

	/**
	 * Saves an TaskRequest
	 * 
	 * @param requestDate
	 * @return RequestDate
	 * @throws APIException
	 */
	@Transactional
	RequestDate saveOrUpdateRequestDate(RequestDate requestDate) throws APIException;
	

	/**
	 * Returns the Last Request Date

	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	RequestDate getLastRequestDate() throws APIException;
}
