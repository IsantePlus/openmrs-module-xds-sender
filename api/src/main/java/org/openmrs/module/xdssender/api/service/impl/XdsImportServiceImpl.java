package org.openmrs.module.xdssender.api.service.impl;

import javassist.NotFoundException;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.dcm4chee.xds2.infoset.ihe.RetrieveDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.ihe.RetrieveDocumentSetRequestType.DocumentRequest;
import org.dcm4chee.xds2.infoset.ihe.RetrieveDocumentSetResponseType;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.openmrs.module.xdssender.api.service.XdsImportService;
import org.openmrs.module.xdssender.api.xds.XdsReceiver;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component("xdsSender.XdsImportService")
public class XdsImportServiceImpl implements XdsImportService {
	
	private final XdsReceiver xdsReceiver = new XdsReceiver();
	
	@Override
	public byte[] retrieveCCD(DocumentInfo documentInfo) throws XDSException, NotFoundException, IOException {
		
		RetrieveDocumentSetRequestType request = new RetrieveDocumentSetRequestType();
		DocumentRequest documentRequest = new DocumentRequest();
		
		documentRequest.setDocumentUniqueId(documentInfo.getUniqueId());
		documentRequest.setRepositoryUniqueId(documentInfo.getRepositoryId());
		
		request.getDocumentRequest().add(documentRequest);
		
		RetrieveDocumentSetResponseType response = xdsReceiver.sendRetrieveCCD(request);
		if (response.getDocumentResponse().size() == 0) {
			throw new NotFoundException("No response returned");
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		response.getDocumentResponse().get(0).getDocument().writeTo(bos);
		
		return bos.toByteArray();
	}
}
