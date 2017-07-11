package org.openmrs.module.xdssender.api.service;

import javassist.NotFoundException;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.openmrs.module.xdssender.api.model.DocumentInfo;

import java.io.IOException;

public interface XdsImportService {
	
	byte[] retrieveCCD(DocumentInfo documentInfo) throws XDSException, NotFoundException, IOException;
}
