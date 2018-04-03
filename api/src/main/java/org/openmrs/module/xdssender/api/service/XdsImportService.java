package org.openmrs.module.xdssender.api.service;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.model.DocumentInfo;

import java.io.IOException;

public interface XdsImportService {
	
	Ccd retrieveCCD(DocumentInfo documentInfo) throws XDSException, IOException;
}
