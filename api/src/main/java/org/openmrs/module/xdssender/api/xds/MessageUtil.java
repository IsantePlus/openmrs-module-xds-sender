package org.openmrs.module.xdssender.api.xds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.dcm4chee.xds2.common.XDSConstants;
import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.AssociationType1;
import org.dcm4chee.xds2.infoset.rim.ClassificationType;
import org.dcm4chee.xds2.infoset.rim.ExtrinsicObjectType;
import org.dcm4chee.xds2.infoset.rim.RegistryObjectListType;
import org.dcm4chee.xds2.infoset.rim.RegistryPackageType;
import org.dcm4chee.xds2.infoset.rim.SubmitObjectsRequest;
import org.dcm4chee.xds2.infoset.rim.VersionInfoType;
import org.dcm4chee.xds2.infoset.util.InfosetUtil;
import org.marc.everest.datatypes.TS;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.cda.CdaDataUtil;
import org.openmrs.module.xdssender.api.model.DocumentData;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("xdssender.MessageUtil")
public class MessageUtil {
	
	private static final String ECID_NAME = "ECID";
	
	private static final String CODE_NATIONAL_NAME = "Code National";
	
	private static final String CODE_ST_NAME = "Code ST";

	private static final String RIM_SOURCE = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";

	private static final String XDS_DOCUMENT_ENTRY_OBJECT_TYPE = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";

	private static final String OUTPATIENT_NOTE_LOINC = "34108-1";

	private static final String SUBMITION_SET_ID = "SubmissionSet01";

	@Autowired
	private CdaDataUtil cdaDataUtil;
	
	@Autowired
	private XdsUtil xdsUtil;
	
	@Autowired
	private XdsSenderConfig config;

	public ProvideAndRegisterDocumentSetRequestType createProvideAndRegisterDocument(DocumentData clinicalData,
																					 DocumentData msgData,
																					 Encounter encounter)
			throws JAXBException {
		// createProvideAndRegisterClinicalDocument can be replaced by methods used to generate the ORM_O01 message
		ProvideAndRegisterDocumentSetRequestType result = createProvideAndRegisterClinicalDocument(
				clinicalData.getDocumentContent(), clinicalData.getDocumentInfo(), encounter);

		if (msgData != null) {
			SubmitObjectsRequest registryRequest = result.getSubmitObjectsRequest();

			ExtrinsicObjectType msgOddRegistry = createExtrinsicObjectType(msgData.getDocumentInfo(), encounter);
			registryRequest
					.getRegistryObjectList()
					.getIdentifiable()
					.add(new JAXBElement<ExtrinsicObjectType>(new QName(RIM_SOURCE,
							"ExtrinsicObject"), ExtrinsicObjectType.class, msgOddRegistry));
			registryRequest = addAssociation(registryRequest, msgOddRegistry, SUBMITION_SET_ID, "as02");
			result = addDocToRequest(result, msgOddRegistry.getId(), msgData.getDocumentContent());

			result.setSubmitObjectsRequest(registryRequest);
		}

		return result;
	}

	// TODO: Add unit test
	public ProvideAndRegisterDocumentSetRequestType createProvideAndRegisterDocument(DocumentData clinicalData,
	        List<DocumentData> additionalData, Encounter encounter) throws JAXBException {
		// createProvideAndRegisterClinicalDocument can be replaced by methods used to generate the ORM_O01 message
		ProvideAndRegisterDocumentSetRequestType result = createProvideAndRegisterClinicalDocument(
		    clinicalData.getDocumentContent(), clinicalData.getDocumentInfo(), encounter);
		
		if (additionalData != null && additionalData.size() > 0) {
			Integer setid = 2;
			
			for (DocumentData msgData : additionalData) {
				SubmitObjectsRequest registryRequest = result.getSubmitObjectsRequest();
				
				ExtrinsicObjectType msgOddRegistry = createExtrinsicObjectType(msgData.getDocumentInfo(), encounter);
				registryRequest.getRegistryObjectList().getIdentifiable().add(new JAXBElement<ExtrinsicObjectType>(
				        new QName(RIM_SOURCE, "ExtrinsicObject"), ExtrinsicObjectType.class, msgOddRegistry));
				registryRequest = addAssociation(registryRequest, msgOddRegistry, SUBMITION_SET_ID, "as" + String.format("%02d", setid));
				result = addDocToRequest(result, msgOddRegistry.getId(), msgData.getDocumentContent());
				
				result.setSubmitObjectsRequest(registryRequest);
				
				setid++;
				
			}
		}
		
		return result;
	}	
	
	public ProvideAndRegisterDocumentSetRequestType createProvideAndRegisterClinicalDocument(byte[] documentContent,
																							 final DocumentInfo info,
																							 Encounter encounter)
			throws JAXBException {
		String patientId = getPatientIdentifier(info).getIdentifier();
		String location = getPatientLocation(info).getName();
		
		ProvideAndRegisterDocumentSetRequestType retVal = new ProvideAndRegisterDocumentSetRequestType();
		SubmitObjectsRequest registryRequest = new SubmitObjectsRequest();
		retVal.setSubmitObjectsRequest(registryRequest);
		
		registryRequest.setRegistryObjectList(new RegistryObjectListType());
		ExtrinsicObjectType oddRegistryObject = new ExtrinsicObjectType();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		// ODD
		if (encounter.getForm() == null) {
			throw new RuntimeException("Cannot send encounter without formId");
		}
		oddRegistryObject.setId(encounter.getLocation().getUuid() + "/" + patientId + "/"
		        + encounter.getEncounterType().getUuid() + "/" + encounter.getForm().getUuid() + "/"
		        + format.format(encounter.getEncounterDatetime()) + "/" + info.getUniqueId());
		oddRegistryObject.setMimeType("text/xml");
		
		//software version
		Module module = ModuleFactory.getModuleById(config.getModuleUsedToDetermineSoftwareVersion());
		if (module != null) {
			VersionInfoType softwareVersion = new VersionInfoType();
			softwareVersion.setVersionName(module.getVersion());
			softwareVersion.setComment("Software Version");
			oddRegistryObject.setContentVersionInfo(softwareVersion);
		}
		
		// Get the earliest time something occurred and the latest
		Date lastEncounter = encounter.getEncounterDatetime(), firstEncounter = encounter.getEncounterDatetime();
		
		if (info.getRelatedEncounter() != null)
			for (Obs el : info.getRelatedEncounter().getObs()) {
				if (el.getObsDatetime().before(firstEncounter))
					firstEncounter = el.getEncounter().getVisit().getStartDatetime();
				if (lastEncounter != null && el.getObsDatetime().after(lastEncounter))
					lastEncounter = el.getEncounter().getEncounterDatetime();
			}
		
		TS firstEncounterTs = cdaDataUtil.createTS(firstEncounter), lastEncounterTs = cdaDataUtil.createTS(lastEncounter);
		TS
		        .now();
		
		firstEncounterTs.setDateValuePrecision(TS.MINUTENOTIMEZONE);
		lastEncounterTs.setDateValuePrecision(TS.MINUTENOTIMEZONE);
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_SERVICE_START_TIME,
		    firstEncounterTs.getValue());
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_SERVICE_STOP_TIME,
		    lastEncounterTs.getValue());
		
		oddRegistryObject.setObjectType("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
		
		PatientIdentifier nationalIdentifier = getNationalPatientIdentifier(info);
		PatientIdentifier siteIdentifier = getSitePatientIdentifier(info);
		
		// Add source patient information
		TS patientDob = cdaDataUtil.createTS(info.getPatient().getBirthdate());
		patientDob.setDateValuePrecision(TS.DAY);
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_SOURCE_PATIENT_ID,
		    String.format("%s^^^&%s&ISO", patientId, config.getEcidRoot()));
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_SOURCE_PATIENT_INFO,
		    String.format("PID-3|%s^^^&%s&ISO", nationalIdentifier.getIdentifier(), config.getCodeNationalRoot()),
		    String.format("PID-3|%s^^^&%s&ISO", siteIdentifier.getIdentifier(), config.getCodeStRoot()),
		    String.format("PID-5|%s^%s^^^", info.getPatient().getFamilyName(), info.getPatient().getGivenName()),
		    String.format("PID-7|%s", patientDob.getValue()), String.format("PID-8|%s", info.getPatient().getGender()),
		    String.format("PID-11|%s", location));
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_LANGUAGE_CODE, Context.getLocale()
		        .toString());
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_CREATION_TIME, new SimpleDateFormat(
		        "yyyyMMddHHmmss").format(new Date()));
		
		// Unique identifier
		xdsUtil.addExtenalIdentifier(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_uniqueId,
		    String.format("2.25.%s", UUID.randomUUID().getLeastSignificantBits()).replaceAll("-", ""),
		    "XDSDocumentEntry.uniqueId");
		xdsUtil.addExtenalIdentifier(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_patientId,
		    String.format("%s^^^%s&%s&NI", patientId, config.getEcidRoot(), config.getEcidRoot()),
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
		regPackage.setId(SUBMITION_SET_ID);
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
		    String.format("%s^^^%s&%s&NI", patientId, config.getEcidRoot(), config.getEcidRoot()),
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
				            setClassifiedObject(SUBMITION_SET_ID);
				            setClassificationNode(XDSConstants.UUID_XDSSubmissionSet);
			            }
		            }));
		
		// Add an association
		AssociationType1 association = new AssociationType1();
		association.setId("as01");
		association.setAssociationType(XDSConstants.HAS_MEMBER);
		association.setSourceObject(SUBMITION_SET_ID);
		association.setTargetObject(encounter.getLocation().getUuid() + "/" + patientId + "/"
		        + encounter.getEncounterType().getUuid() + "/" + encounter.getForm().getUuid() + "/"
		        + format.format(encounter.getEncounterDatetime()) + "/" + info.getUniqueId());
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
			
			String siteCode = null;
			
			for (LocationAttribute attribute : info.getRelatedEncounter().getLocation().getAttributes()) {
				if (attribute.getAttributeType().getUuid().equals(XdsSenderConstants.LOCATION_SITECODE_ATTRIBUTE_UUID)) {
					siteCode = attribute.getValue().toString();
				}
			}
			
			String institutionText = String.format("%s^^^^^&%s&ISO^^^^%s", info.getRelatedEncounter().getLocation()
			        .getName(), config.getLocationRoot(), siteCode);
			
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
	
	public PatientIdentifier getPatientIdentifier(DocumentInfo info) {
		PatientIdentifier result = info.getPatient().getPatientIdentifier();
		
		for (PatientIdentifier pid : info.getPatient().getIdentifiers()) {
			if (pid.getIdentifierType().getName().equals(ECID_NAME)) {
				result = pid;
			}
		}
		return result;
	}
	
	public PatientIdentifier getNationalPatientIdentifier(DocumentInfo info) {
		PatientIdentifier result = info.getPatient().getPatientIdentifier();
		
		for (PatientIdentifier pid : info.getPatient().getIdentifiers()) {
			if (pid.getIdentifierType().getName().equals(CODE_NATIONAL_NAME)) {
				result = pid;
			}
		}
		return result;
	}
	
	public PatientIdentifier getSitePatientIdentifier(DocumentInfo info) {
		PatientIdentifier result = info.getPatient().getPatientIdentifier();
		
		for (PatientIdentifier pid : info.getPatient().getIdentifiers()) {
			if (pid.getIdentifierType().getName().equals(CODE_ST_NAME)) {
				result = pid;
			}
		}
		return result;
	}
	
	public Location getPatientLocation(DocumentInfo info) {
		return info.getPatient().getPatientIdentifier().getLocation();
	}

	private ProvideAndRegisterDocumentSetRequestType addDocToRequest(ProvideAndRegisterDocumentSetRequestType requestType,
																	 String oddRegId, byte[] docContent) {
		ProvideAndRegisterDocumentSetRequestType.Document doc = new ProvideAndRegisterDocumentSetRequestType.Document();
		doc.setId(oddRegId);
		doc.setValue(docContent);

		requestType.getDocument().add(doc);

		return requestType;
	}

	private SubmitObjectsRequest addAssociation(SubmitObjectsRequest registryRequest, ExtrinsicObjectType extrinsicObject,
												String registryPackageId, String associationId) throws JAXBException {
		AssociationType1 association = new AssociationType1();
		association.setId(associationId);
		association.setAssociationType(XDSConstants.HAS_MEMBER);
		association.setSourceObject(registryPackageId);
		association.setTargetObject(extrinsicObject.getId());
		InfosetUtil.addOrOverwriteSlot(association, XDSConstants.SLOT_NAME_SUBMISSIONSET_STATUS, "Original");
		registryRequest
				.getRegistryObjectList()
				.getIdentifiable()
				.add(new JAXBElement<AssociationType1>(
						new QName(RIM_SOURCE, "Association"), AssociationType1.class,
						association));

		return registryRequest;
	}

	private ExtrinsicObjectType createExtrinsicObjectType(DocumentInfo info, Encounter encounter) throws JAXBException {
		String patientId = getPatientIdentifier(info).getIdentifier();
		String location = getPatientLocation(info).getName();

		ExtrinsicObject extrinsicObject = new ExtrinsicObject(patientId, location);
		extrinsicObject.setId(encounter, info);
		extrinsicObject.setMimeType(info.getMimeType());
		extrinsicObject.setContentVersionInfo(config.getModuleUsedToDetermineSoftwareVersion());

		// Get the earliest time something occurred and the latest
		Date lastEncounter = encounter.getEncounterDatetime(), firstEncounter = encounter.getEncounterDatetime();
		if (info.getRelatedEncounter() != null)
			for (Obs el : info.getRelatedEncounter().getObs()) {
				if (el.getObsDatetime().before(firstEncounter))
					firstEncounter = el.getEncounter().getVisit().getStartDatetime();
				if (lastEncounter != null && el.getObsDatetime().after(lastEncounter))
					lastEncounter = el.getEncounter().getEncounterDatetime();
			}

		TS firstEncounterTs = cdaDataUtil.createTS(firstEncounter);
		TS lastEncounterTs = cdaDataUtil.createTS(lastEncounter);
		extrinsicObject.addOrOverwriteSlot(firstEncounterTs, lastEncounterTs);

		extrinsicObject.setObjectType(XDS_DOCUMENT_ENTRY_OBJECT_TYPE);

		// Add source patient information
		PatientIdentifier nationalIdentifier = getNationalPatientIdentifier(info);
		PatientIdentifier siteIdentifier = getSitePatientIdentifier(info);
		TS patientDob = cdaDataUtil.createTS(info.getPatient().getBirthdate());
		patientDob.setDateValuePrecision(TS.DAY);
		extrinsicObject.addSourcePatientInformation(info, nationalIdentifier, siteIdentifier, config, patientDob);

		addUniqueIdentifier(extrinsicObject, patientId);

		setClassifications(extrinsicObject, info);

		extrinsicObject.addAuthor(info, config);

		return extrinsicObject;
	}

	private void addUniqueIdentifier(ExtrinsicObjectType extrinsicObject, String patientId) throws JAXBException {
		xdsUtil.addExtenalIdentifier(extrinsicObject, XDSConstants.UUID_XDSDocumentEntry_uniqueId,
				String.format("2.25.%s", UUID.randomUUID().getLeastSignificantBits()).replaceAll("-", ""),
				"XDSDocumentEntry.uniqueId");
		xdsUtil.addExtenalIdentifier(extrinsicObject, XDSConstants.UUID_XDSDocumentEntry_patientId,
				String.format("%s^^^%s&%s&NI", patientId, config.getEcidRoot(), config.getEcidRoot()),
				"XDSDocumentEntry.patientId");
	}

	private void setClassifications(ExtrinsicObjectType extrinsicObject, DocumentInfo info) throws JAXBException {
		xdsUtil.addCodedValueClassification(extrinsicObject, XDSConstants.UUID_XDSDocumentEntry_classCode,
				info.getClassCode(), info.getClassCodeScheme(), "XDSDocumentEntry.classCode");
		xdsUtil.addCodedValueClassification(extrinsicObject, XDSConstants.UUID_XDSDocumentEntry_confidentialityCode,
				"1.3.6.1.4.1.21367.2006.7.101", "Connect-a-thon confidentialityCodes", "XDSDocumentEntry.confidentialityCode");
		xdsUtil.addCodedValueClassification(extrinsicObject, XDSConstants.UUID_XDSDocumentEntry_formatCode,
				info.getFormatCode(), "Connect-a-thon formatCodes", "XDSDocumentEntry.formatCode");
		xdsUtil.addCodedValueClassification(extrinsicObject,
				XDSConstants.UUID_XDSDocumentEntry_healthCareFacilityTypeCode, "Outpatient",
				"Connect-a-thon healthcareFacilityTypeCodes", "XDSDocumentEntry.healthCareFacilityTypeCode");
		xdsUtil.addCodedValueClassification(extrinsicObject, XDSConstants.UUID_XDSDocumentEntry_practiceSettingCode,
				"General Medicine", "Connect-a-thon practiceSettingCodes", "UUID_XDSDocumentEntry.practiceSettingCode");
		xdsUtil.addCodedValueClassification(extrinsicObject, XDSConstants.UUID_XDSDocumentEntry_typeCode,
				OUTPATIENT_NOTE_LOINC, "LOINC", "XDSDocumentEntry.typeCode");
	}
}
