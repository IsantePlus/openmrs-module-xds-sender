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

public class XdsExportServiceImpl extends BaseOpenmrsService implements XdsExportService {

	private final Log log = LogFactory.getLog(this.getClass());

	private ClinicalDocumentBuilder clinicalDocBuilder;

	private ORM_O01DocumentBuilder ormDocBuilder;

	private FhirResourceDocumentBuilder fhirResourceBuilder;

	private MessageUtil messageUtil;

	private XdsSenderConfig config;

	private XdsSender xdsSender;

	public void setClinicalDocBuilder(ClinicalDocumentBuilder clinicalDocBuilder) {
		this.clinicalDocBuilder = clinicalDocBuilder;
	}

	public void setOrmDocBuilder(ORM_O01DocumentBuilder ormDocBuilder) {
		this.ormDocBuilder = ormDocBuilder;
	}

	public void setFhirResourceBuilder(FhirResourceDocumentBuilder fhirResourceBuilder) {
		this.fhirResourceBuilder = fhirResourceBuilder;
	}

	public void setMessageUtil(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	public void setConfig(XdsSenderConfig config) {
		this.config = config;
	}

	public void setXdsSender(XdsSender xdsSender) {
		this.xdsSender = xdsSender;
	}

	@Override
	public DocumentInfo exportProvideAndRegister(Encounter encounter, Patient patient) {
		try {

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

			// Assign a placeholder System Identifier to be used for validating against the MPI
			// patient.addIdentifier(XdsUtil.getPlaceholderSystemIdentifier(patient));
			DocumentData patientFhirResourceDoc = null;
			DocumentModel patientFhirResourceDocModel = fhirResourceBuilder.buildDocument(patient, encounter);
			if (patientFhirResourceDocModel != null) {
				DocumentInfo patientFhirResourceDocInfo = new DocumentInfo(encounter, patient, patientFhirResourceDocModel,
						"text/fhir", config.getProviderRoot());
				patientFhirResourceDoc = new DocumentData(patientFhirResourceDocInfo, patientFhirResourceDocModel.getData());
				
				additionalData.add(patientFhirResourceDoc);
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
