package org.openmrs.module.xdssender.api.service;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.api.domain.Ccd;

public interface XdsImportService {
	
	Ccd retrieveCCD(Patient patient) throws XDSException;
}
