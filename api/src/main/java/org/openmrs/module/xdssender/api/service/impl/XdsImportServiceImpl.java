package org.openmrs.module.xdssender.api.service.impl;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.dcm4chee.xds2.infoset.ihe.RetrieveDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.ihe.RetrieveDocumentSetRequestType.DocumentRequest;
import org.dcm4chee.xds2.infoset.ihe.RetrieveDocumentSetResponseType;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.openmrs.module.xdssender.api.service.XdsImportService;
import org.openmrs.module.xdssender.api.xds.XdsRetriever;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component("xdsSender.XdsImportService")
public class XdsImportServiceImpl implements XdsImportService {
	
	private final XdsRetriever xdsRetriever = new XdsRetriever();
	
	@Override
	public Ccd retrieveCCD(DocumentInfo documentInfo) throws XDSException, IOException {
		
		RetrieveDocumentSetRequestType request = new RetrieveDocumentSetRequestType();
		DocumentRequest documentRequest = new DocumentRequest();
		
		documentRequest.setDocumentUniqueId(documentInfo.getUniqueId());
		documentRequest.setRepositoryUniqueId(documentInfo.getRepositoryId());
		
		request.getDocumentRequest().add(documentRequest);
		
		RetrieveDocumentSetResponseType response = xdsRetriever.sendRetrieveCCD(request);
		if (response.getDocumentResponse().size() == 0) {
			return null;
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		response.getDocumentResponse().get(0).getDocument().writeTo(bos);
		
		String documentContent = new String(bos.toByteArray(), StandardCharsets.UTF_8);
		
		Ccd ccd = new Ccd();
		
		ccd.setPatient(documentInfo.getPatient());
		ccd.setDocument(documentContent);
		
		return ccd;
	}
}
