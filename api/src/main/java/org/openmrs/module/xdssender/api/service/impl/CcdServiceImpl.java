package org.openmrs.module.xdssender.api.service.impl;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.domain.dao.CcdDao;
import org.openmrs.module.xdssender.api.service.CcdService;
import org.openmrs.module.xdssender.api.service.XdsImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Service(value = "xdsSender.CcdService")
public class CcdServiceImpl implements CcdService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CcdServiceImpl.class);
	
	@Autowired
	private XdsImportService xdsImportService;
	
	@Autowired
	private CcdDao ccdDao;
	
	@Override
	public Ccd getLocallyStoredCcd(Patient patient) {
		return ccdDao.find(patient);
	}
	
	@Override
	public Ccd downloadAndSaveCcd(Patient patient) throws XDSException, IOException, KeyStoreException,
	        NoSuchAlgorithmException, KeyManagementException {
		
		Ccd ccd = xdsImportService.retrieveCCD(patient);
		
		if (ccd != null) {
			ccd = ccdDao.saveOrUpdate(ccd);
		}
		
		return ccd;
	}
	
	@Override
	public void downloadCcdAsPDF(OutputStream stream, Patient patient) {
		LOGGER.info("CCD PDF is being downloaded.");
	}
}
