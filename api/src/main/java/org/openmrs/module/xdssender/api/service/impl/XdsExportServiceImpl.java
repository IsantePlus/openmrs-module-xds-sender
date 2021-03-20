package org.openmrs.module.xdssender.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType.Document;
import org.dcm4chee.xds2.infoset.rim.IdentifiableType;
import org.dcm4chee.xds2.infoset.rim.RegistryResponseType;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.cda.ClinicalDocumentBuilder;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.module.xdssender.api.fhir.FhirResourceDocumentBuilder;
import org.openmrs.module.xdssender.api.fhir.exceptions.ResourceGenerationException;
import org.openmrs.module.xdssender.api.hl7.ORM_O01DocumentBuilder;
import org.openmrs.module.xdssender.api.model.DocumentData;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.openmrs.module.xdssender.api.service.XdsExportService;
import org.openmrs.module.xdssender.api.xds.MessageUtil;
import org.openmrs.module.xdssender.api.xds.XdsSender;
import org.openmrs.module.xdssender.api.xds.XdsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("xdsSender.XdsExportService")
public class XdsExportServiceImpl extends BaseOpenmrsService implements XdsExportService {

	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	private ClinicalDocumentBuilder clinicalDocBuilder;

	@Autowired
	private ORM_O01DocumentBuilder ormDocBuilder;
	
	@Autowired 
	private FhirResourceDocumentBuilder fhirResourceBuilder;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private XdsSenderConfig config;

	@Autowired
	private XdsSender xdsSender;

	@Override
	public DocumentInfo exportProvideAndRegister(Encounter encounter, Patient patient) {
		try {
			// Assign a placeholder System Identifier to be used for validating against the MPI
			patient.addIdentifier(XdsUtil.getPlaceholderSystemIdentifier(patient));

			DocumentModel clinicalDocModel = clinicalDocBuilder.buildDocument(patient, encounter);
			DocumentInfo clinicalDocInfo = new DocumentInfo(encounter, patient, clinicalDocModel,
					"text/xsl", config.getProviderRoot());
			DocumentData clinicalDoc = new DocumentData(clinicalDocInfo, clinicalDocModel.getData());
			
			List<DocumentData> additionalData = new ArrayList<DocumentData>();

			DocumentData labOrderDoc = null;
			DocumentModel labOrderDocModel = ormDocBuilder.buildDocument(encounter);
			if (labOrderDocModel != null) {
				DocumentInfo labOrderDocInfo = new DocumentInfo(encounter, patient, labOrderDocModel,
						"text/plain", config.getProviderRoot());
				labOrderDoc = new DocumentData(labOrderDocInfo, labOrderDocModel.getData());
				additionalData.add(labOrderDoc);
			}

			DocumentData patientFhirResourceDoc = null;
			DocumentModel patientFhirResourceDocModel = fhirResourceBuilder.buildDocument(patient, encounter);
			if (patientFhirResourceDocModel != null) {
				DocumentInfo patientFhirResourceDocInfo = new DocumentInfo(encounter, patient, patientFhirResourceDocModel,
						"text/plain", config.getProviderRoot());
				patientFhirResourceDoc = new DocumentData(patientFhirResourceDocInfo, patientFhirResourceDocModel.getData());
				
				additionalData.add(patientFhirResourceDoc);
			}			
			
			// TODO: Replace the ECID with the Golden Record in a configurable fashion (Using global configs)
			if (!messageUtil.getPatientIdentifier(clinicalDocInfo).getIdentifierType().getName().equals("ECID")) {
				throw new Exception("Patient doesn't have ECID Identifier.");
			}

			ProvideAndRegisterDocumentSetRequestType request = messageUtil.createProvideAndRegisterDocument(clinicalDoc,
					additionalData, encounter);

			logRequest(request);
			
			RegistryResponseType response = xdsSender.sendProvideAndRegister(request);

			if (!response.getStatus().contains("Success"))
				throw new Exception("Could not execute provide and register");

			return clinicalDocInfo;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void logRequest(ProvideAndRegisterDocumentSetRequestType req) {
		log.debug("###### SubmitObjectRequest:"+req.getSubmitObjectsRequest());
		List<Document> docs = req.getDocument();
		log.debug("###### Documents:"+docs);
		if (docs != null) {
		  StringBuilder sb = new StringBuilder();
		  sb.append("######Number of Documents:").append(docs.size());
		  int dumpValLen;
		  for (Document d : docs) {
			sb.append("\nDocument ID:"+d.getId())
			.append("       size:").append(d.getValue().length)
			.append("       value:");
			try {
			  dumpValLen = Math.min(d.getValue().length, 40);
			  sb.append(new String(d.getValue(), 0, dumpValLen));
			  if (dumpValLen == 40)
				sb.append("...");
			} catch (Exception x) {
			  log.warn("Failed to convert value in String!", x);
			}
		  }
		  log.debug(sb.toString());
		}
	  }

}
