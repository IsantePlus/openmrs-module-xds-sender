package org.openmrs.module.xdssender.api.service.impl;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.domain.dao.CcdDao;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.openmrs.module.xdssender.api.service.CcdService;
import org.openmrs.module.xdssender.api.service.XdsImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CcdServiceImpl implements CcdService {
	
	@Autowired
	private XdsImportService xdsImportService;
	
	@Autowired
	private CcdDao ccdDao;
	
	@Override
	public Ccd getLocallyStoredCcd(Patient patient) {
		return ccdDao.find(patient);
	}
	
	@Override
	public Ccd downloadAndSaveCcd(Patient patient) throws XDSException, IOException {
		DocumentInfo info = new DocumentInfo();
		info.setPatient(patient);
		
		Ccd ccd = xdsImportService.retrieveCCD(info);
		
		if (ccd != null) {
			ccd = ccdDao.saveOrUpdate(ccd);
		}
		
		return ccd;
	}
}
