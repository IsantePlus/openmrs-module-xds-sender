package org.openmrs.module.xdssender.api.xds;

import org.dcm4chee.xds2.common.XDSConstants;
import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.AssociationType1;
import org.dcm4chee.xds2.infoset.rim.ClassificationType;
import org.dcm4chee.xds2.infoset.rim.ExtrinsicObjectType;
import org.dcm4chee.xds2.infoset.rim.RegistryObjectListType;
import org.dcm4chee.xds2.infoset.rim.RegistryPackageType;
import org.dcm4chee.xds2.infoset.rim.SubmitObjectsRequest;
import org.dcm4chee.xds2.infoset.util.InfosetUtil;
import org.marc.everest.datatypes.TS;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.cda.CdaDataUtil;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component("xdssender.MessageUtil")
public class MessageUtil {
	
	private static final String ECID_NAME = "ECID";
	
	@Autowired
	private CdaDataUtil cdaDataUtil;
	
	@Autowired
	private XdsUtil xdsUtil;
	
	@Autowired
	private XdsSenderConfig config;
	
	public ProvideAndRegisterDocumentSetRequestType createProvideAndRegisterDocument(byte[] documentContent,
	        final DocumentInfo info, Encounter encounter) throws JAXBException, IOException {
		String patientEcidId = getEcidPatientIdentifier(info).getIdentifier();
		String patientIsantePlusId = getIsantePusPatientIdentifier(info).getIdentifier();
		
		ProvideAndRegisterDocumentSetRequestType retVal = new ProvideAndRegisterDocumentSetRequestType();
		SubmitObjectsRequest registryRequest = new SubmitObjectsRequest();
		retVal.setSubmitObjectsRequest(registryRequest);
		
		registryRequest.setRegistryObjectList(new RegistryObjectListType());
		ExtrinsicObjectType oddRegistryObject = new ExtrinsicObjectType();
		// ODD
		oddRegistryObject.setId(encounter.getLocation().toString() + ":" + patientIsantePlusId + ":"
		        + encounter.getForm().getName() + ":" + encounter.getEncounterDatetime());
		oddRegistryObject.setMimeType("text/xml");
		
		// Get the earliest time something occurred and the latest
		Date lastEncounter = encounter.getEncounterDatetime(), firstEncounter = encounter.getEncounterDatetime();
		
		if (info.getRelatedEncounter() != null)
			for (Obs el : info.getRelatedEncounter().getObs()) {
				if (el.getObsDatetime().before(firstEncounter))
					firstEncounter = el.getEncounter().getVisit().getStartDatetime();
				if (lastEncounter != null && el.getObsDatetime().after(lastEncounter))
					lastEncounter = el.getEncounter().getEncounterDatetime();
			}
		
		TS firstEncounterTs = cdaDataUtil.createTS(firstEncounter), lastEncounterTs = cdaDataUtil.createTS(lastEncounter), creationTimeTs = TS
		        .now();
		
		firstEncounterTs.setDateValuePrecision(TS.MINUTENOTIMEZONE);
		lastEncounterTs.setDateValuePrecision(TS.MINUTENOTIMEZONE);
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_SERVICE_START_TIME,
		    firstEncounterTs.getValue());
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_SERVICE_STOP_TIME,
		    lastEncounterTs.getValue());
		
		oddRegistryObject.setObjectType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
		
		// Add source patient information
		TS patientDob = cdaDataUtil.createTS(info.getPatient().getBirthdate());
		patientDob.setDateValuePrecision(TS.DAY);
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_SOURCE_PATIENT_ID,
		    String.format("%s^^^&%s&ISO", patientEcidId, config.getPatientRoot()));
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_SOURCE_PATIENT_INFO,
		    String.format("PID-3|%s^^^&%s&ISO", patientEcidId, config.getPatientRoot()),
		    String.format("PID-5|%s^%s^^^", info.getPatient().getFamilyName(), info.getPatient().getGivenName()),
		    String.format("PID-7|%s", patientDob.getValue()), String.format("PID-8|%s", info.getPatient().getGender()));
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_LANGUAGE_CODE, Context.getLocale()
		        .toString());
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_CREATION_TIME, new SimpleDateFormat(
		        "yyyyMMddHHmmss").format(new Date()));
		
		// Unique identifier
		xdsUtil.addExtenalIdentifier(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_uniqueId,
		    String.format("2.25.%s", UUID.randomUUID().getLeastSignificantBits()).replaceAll("-", ""),
		    "XDSDocumentEntry.uniqueId");
		xdsUtil.addExtenalIdentifier(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_patientId,
		    String.format("%s^^^%s&%s&NI", patientEcidId, config.getEcidRoot(), config.getEcidRoot()),
		    "XDSDocumentEntry.patientId");
		
		// Set classifications
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_classCode,
		    info.getClassCode(), "LOINC", "XDSDocumentEntry.classCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_confidentialityCode,
		    "1.3.6.1.4.1.21367.2006.7.101", "Connect-a-thon confidentialityCodes", "XDSDocumentEntry.confidentialityCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_formatCode,
		    info.getFormatCode(), "Connect-a-thon formatCodes", "XDSDocumentEntry.formatCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject,
		    XDSConstants.UUID_XDSDocumentEntry_healthCareFacilityTypeCode, "Outpatient",
		    "Connect-a-thon healthcareFacilityTypeCodes", "XDSDocumentEntry.healthCareFacilityTypeCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_practiceSettingCode,
		    "General Medicine", "Connect-a-thon practiceSettingCodes", "UUID_XDSDocumentEntry.practiceSettingCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_typeCode, "34108-1",
		    "LOINC", "XDSDocumentEntry.typeCode");
		
		// Create the submission set
		TS now = TS.now();
		now.setDateValuePrecision(TS.SECONDNOTIMEZONE);
		
		RegistryPackageType regPackage = new RegistryPackageType();
		regPackage.setId("SubmissionSet01");
		InfosetUtil.addOrOverwriteSlot(regPackage, XDSConstants.SLOT_NAME_SUBMISSION_TIME, now.getValue());
		xdsUtil.addCodedValueClassification(regPackage, XDSConstants.UUID_XDSSubmissionSet_contentTypeCode,
		    info.getClassCode(), "LOINC", "XDSSubmissionSet.contentTypeCode");
		
		// Submission set external identifiers
		xdsUtil.addExtenalIdentifier(regPackage, XDSConstants.UUID_XDSSubmissionSet_uniqueId,
		    String.format("2.25.%s", UUID.randomUUID().getLeastSignificantBits()).replaceAll("-", ""),
		    "XDSSubmissionSet.uniqueId");
		xdsUtil.addExtenalIdentifier(regPackage, XDSConstants.UUID_XDSSubmissionSet_sourceId,
		    String.format("2.25.%s", UUID.randomUUID().getLeastSignificantBits()).replaceAll("-", ""),
		    "XDSSubmissionSet.sourceId");
		xdsUtil.addExtenalIdentifier(regPackage, XDSConstants.UUID_XDSSubmissionSet_patientId,
		    String.format("%s^^^%s&%s&NI", patientEcidId, config.getEcidRoot(), config.getEcidRoot()),
		    "XDSSubmissionSet.patientId");
		
		// Add the eo to the submission
		registryRequest
		        .getRegistryObjectList()
		        .getIdentifiable()
		        .add(
		            new JAXBElement<ExtrinsicObjectType>(new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0",
		                    "ExtrinsicObject"), ExtrinsicObjectType.class, oddRegistryObject));
		
		// Add the package to the submission
		registryRequest
		        .getRegistryObjectList()
		        .getIdentifiable()
		        .add(
		            new JAXBElement<RegistryPackageType>(new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0",
		                    "RegistryPackage"), RegistryPackageType.class, regPackage));
		
		// Add classification for the submission set
		registryRequest
		        .getRegistryObjectList()
		        .getIdentifiable()
		        .add(
		            new JAXBElement<ClassificationType>(new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0",
		                    "Classification"), ClassificationType.class, new ClassificationType() {
			            
			            {
				            setId("cl01");
				            setClassifiedObject("SubmissionSet01");
				            setClassificationNode(XDSConstants.UUID_XDSSubmissionSet);
			            }
		            }));
		
		// Add an association
		AssociationType1 association = new AssociationType1();
		association.setId("as01");
		association.setAssociationType(XDSConstants.HAS_MEMBER);
		association.setSourceObject("SubmissionSet01");
		association.setTargetObject("Document01");
		InfosetUtil.addOrOverwriteSlot(association, XDSConstants.SLOT_NAME_SUBMISSIONSET_STATUS, "Original");
		registryRequest
		        .getRegistryObjectList()
		        .getIdentifiable()
		        .add(
		            new JAXBElement<AssociationType1>(
		                    new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "Association"), AssociationType1.class,
		                    association));
		
		// Add author
		List<String> authors = new ArrayList<String>();
		
		for (Provider pvdr : info.getAuthors()) {
			ClassificationType authorClass = new ClassificationType();
			authorClass.setClassificationScheme(XDSConstants.UUID_XDSDocumentEntry_author);
			authorClass.setClassifiedObject(oddRegistryObject.getId());
			authorClass.setNodeRepresentation("");
			authorClass.setId(String.format("Classification_%s", UUID.randomUUID().toString()));
			
			String authorText = String.format("%s^%s^%s^^^^^^&%s&ISO", pvdr.getIdentifier(), pvdr.getPerson()
			        .getFamilyName(), pvdr.getPerson().getGivenName(), config.getProviderRoot());
			if (authors.contains(authorText))
				continue;
			else
				authors.add(authorText);
			
			String institutionText = String.format("%s^^^^^&%s&ISO^^^^%s", info.getRelatedEncounter().getLocation()
			        .getName(), config.getLocationRoot(), info.getRelatedEncounter().getLocation().getId());
			
			InfosetUtil.addOrOverwriteSlot(authorClass, XDSConstants.SLOT_NAME_AUTHOR_PERSON, authorText);
			InfosetUtil.addOrOverwriteSlot(authorClass, "authorInstitution", institutionText);
			oddRegistryObject.getClassification().add(authorClass);
		}
		
		ProvideAndRegisterDocumentSetRequestType.Document doc = new ProvideAndRegisterDocumentSetRequestType.Document();
		doc.setId(oddRegistryObject.getId());
		doc.setValue(documentContent);
		retVal.getDocument().add(doc);
		
		return retVal;
	}
	
	public PatientIdentifier getEcidPatientIdentifier(DocumentInfo info) {
		PatientIdentifier result = info.getPatient().getPatientIdentifier();
		
		for (PatientIdentifier pid : info.getPatient().getIdentifiers()) {
			if (pid.getIdentifierType().getName().equals(ECID_NAME)) {
				result = pid;
			}
		}
		return result;
	}
	
	public PatientIdentifier getIsantePusPatientIdentifier(DocumentInfo info) {
		PatientIdentifier result = info.getPatient().getPatientIdentifier();
		
		for (PatientIdentifier pid : info.getPatient().getIdentifiers()) {
			if (pid.getIdentifierType().getUuid().equals(XdsSenderConstants.ISANTEPLUS_IDENTIFIER_UUID)) {
				result = pid;
			}
		}
		return result;
	}
}
