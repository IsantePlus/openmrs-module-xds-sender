package org.openmrs.module.xdssender.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.RegistryResponseType;
import org.marc.everest.datatypes.II;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Author;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.labintegration.api.hl7.OrderGeneratorManager;
import org.openmrs.module.labintegration.api.hl7.messages.MessageCreationException;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.cda.ClinicalDocumentBuilder;
import org.openmrs.module.xdssender.api.cda.ORM_O01DocumentBuilder;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.openmrs.module.labintegration.api.model.OrderDestination;
import org.openmrs.module.xdssender.api.service.XdsExportService;
import org.openmrs.module.xdssender.api.xds.MessageUtil;
import org.openmrs.module.xdssender.api.xds.XdsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component("xdsSender.XdsExportService")
public class XdsExportServiceImpl extends BaseOpenmrsService implements XdsExportService {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private ClinicalDocumentBuilder docBuilder;
	
	@Autowired
	private ORM_O01DocumentBuilder ormDocBuilder;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Autowired
	private XdsSenderConfig config;
	
	@Autowired
	private XdsSender xdsSender;
	
	@Autowired
	private OrderGeneratorManager orderGeneratorManager;
	
	@Override
	public DocumentInfo exportProvideAndRegister(Encounter encounter, Patient patient) {
		try {
            List<byte[]> modelData = new ArrayList<>();

			DocumentModel docModel = docBuilder.buildDocument(patient, encounter);
			DocumentInfo docInfo = buildDocInfo(encounter, patient, docModel);
			modelData.add(docModel.getData());

            modelData = addORM_O01IfNeeded(encounter, modelData);

			if (!messageUtil.getPatientIdentifier(docInfo).getIdentifierType().getName().equals("ECID")) {
				throw new Exception("Patient doesn't have ECID Identifier.");
			}

			ProvideAndRegisterDocumentSetRequestType request = messageUtil.createProvideAndRegisterDocument(docInfo,
					encounter, modelData);
			RegistryResponseType response = xdsSender.sendProvideAndRegister(request);

			if (!response.getStatus().contains("Success"))
				throw new Exception("Could not execute provide and register");

			return docInfo;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
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
		
		List<Provider> provs = new ArrayList<Provider>();
		for (Author aut : docModel.getDoc().getAuthor()) {
			// Load the author
			for (II id : aut.getAssignedAuthor().getId())
				if (id.getRoot() != null && id.getRoot().equals(config.getProviderRoot()))
					provs.add(Context.getProviderService().getProvider(Integer.parseInt(id.getExtension())));
		}
		docInfo.setAuthors(provs);
		
		return docInfo;
	}
	
	private List<byte[]> addORM_O01IfNeeded(Encounter encounter, List<byte[]> modelData) {
	        if (modelData == null) {
	            modelData = new ArrayList<>();
	        }

	        try {
	            List<String> messages = orderGeneratorManager.generateOrders(encounter, OrderDestination.SCC);

	            for (String msg : messages) {
	                DocumentModel docModel = ormDocBuilder.buildDocument(msg);
	                modelData.add(docModel.getData());
	            }
	        } catch (MessageCreationException ex) {
	            log.error("Error generating orders:", ex);
	        }

	        return modelData;
	    }
}
