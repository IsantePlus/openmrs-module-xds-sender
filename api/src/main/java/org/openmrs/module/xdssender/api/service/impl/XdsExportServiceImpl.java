package org.openmrs.module.xdssender.api.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.RegistryResponseType;
import org.marc.everest.datatypes.II;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Author;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.cda.ClinicalDocumentBuilder;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.module.xdssender.api.errorhandling.ErrorHandlingService;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.openmrs.module.xdssender.api.service.XdsExportService;
import org.openmrs.module.xdssender.api.xds.MessageUtil;
import org.openmrs.module.xdssender.api.xds.XdsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component("xdsSender.XdsExportService")
public class XdsExportServiceImpl extends BaseOpenmrsService implements XdsExportService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XdsExportServiceImpl.class);
	
	@Autowired
	private ClinicalDocumentBuilder docBuilder;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Autowired
	private XdsSenderConfig config;
	
	@Autowired
	private XdsSender xdsSender;
	
	@Override
	public DocumentInfo exportProvideAndRegister(Encounter encounter, Patient patient) {
		try {
			DocumentModel docModel = docBuilder.buildDocument(patient, encounter);
			
			DocumentInfo docInfo = buildDocInfo(encounter, patient, docModel);
			
			if (!messageUtil.getPatientIdentifier(docInfo).getIdentifierType().getName().equals("ECID")) {
				throw new Exception("Patient doesn't have ECID Identifier.");
			}
			
			ProvideAndRegisterDocumentSetRequestType request = messageUtil.createProvideAndRegisterDocument(
			    docModel.getData(), docInfo, encounter);
			RegistryResponseType response = xdsSender.sendProvideAndRegister(request);
			
			if (!response.getStatus().contains("Success"))
				throw new Exception("Could not execute provide and register");
			
			return docInfo;
		} catch (Exception e) {
			ErrorHandlingService errorHandler = config.getXdsBErrorHandlingService();
			if (errorHandler != null) {
				LOGGER.error("XDS export exception occurred", e);
				errorHandler.handle(e.getMessage(),
						"xdsSender.XdsExportService.exportProvideAndRegister", true,
						ExceptionUtils.getFullStackTrace(e));
			} else {
				throw new RuntimeException("XDS export exception occurred "
						+ "with not configured XDS.b error handler", e);
			}
		}
		return null;
	}
	
	private DocumentInfo buildDocInfo(Encounter encounter, Patient patient, DocumentModel docModel) {
		DocumentInfo docInfo = new DocumentInfo();
		docInfo.setUniqueId(String.format("2.25.%s", UUID.randomUUID().getMostSignificantBits()).replaceAll("-", ""));
		
		docInfo.setRelatedEncounter(encounter);
		
		docInfo.setClassCode(docModel.getTypeCode());
		docInfo.setFormatCode(docModel.getFormatCode());
		docInfo.setCreationTime(new Date());
		docInfo.setMimeType("text/xml");
		docInfo.setPatient(patient);
		
		List<Provider> provs = new ArrayList<>();
		for (Author aut : docModel.getDoc().getAuthor()) {
			// Load the author
			for (II id : aut.getAssignedAuthor().getId())
				if (id.getRoot() != null && id.getRoot().equals(config.getProviderRoot()))
					provs.add(Context.getProviderService().getProvider(Integer.parseInt(id.getExtension())));
		}
		docInfo.setAuthors(provs);
		
		return docInfo;
	}
}
