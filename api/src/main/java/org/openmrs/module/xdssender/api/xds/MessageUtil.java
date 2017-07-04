package org.openmrs.module.xdssender.api.xds;

import org.dcm4chee.xds2.common.XDSConstants;
import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.AssociationType1;
import org.dcm4chee.xds2.infoset.rim.ClassificationType;
import org.dcm4chee.xds2.infoset.rim.ExtrinsicObjectType;
import org.dcm4chee.xds2.infoset.rim.InternationalStringType;
import org.dcm4chee.xds2.infoset.rim.LocalizedStringType;
import org.dcm4chee.xds2.infoset.rim.RegistryObjectListType;
import org.dcm4chee.xds2.infoset.rim.RegistryPackageType;
import org.dcm4chee.xds2.infoset.rim.SubmitObjectsRequest;
import org.dcm4chee.xds2.infoset.util.InfosetUtil;
import org.marc.everest.datatypes.TS;
import org.openmrs.Obs;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.XdsSenderConfig;
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
	
	@Autowired
	private CdaDataUtil cdaDataUtil;
	
	@Autowired
	private XdsUtil xdsUtil;
	
	@Autowired
	private XdsSenderConfig config;
	
	public ProvideAndRegisterDocumentSetRequestType createProvideAndRegisterDocument(byte[] documentContent,
	        final DocumentInfo info) throws JAXBException, IOException {
		
		ProvideAndRegisterDocumentSetRequestType retVal = new ProvideAndRegisterDocumentSetRequestType();
		SubmitObjectsRequest registryRequest = new SubmitObjectsRequest();
		retVal.setSubmitObjectsRequest(registryRequest);
		
		registryRequest.setRegistryObjectList(new RegistryObjectListType());
		ExtrinsicObjectType oddRegistryObject = new ExtrinsicObjectType();
		// ODD
		oddRegistryObject.setId("Document01");
		oddRegistryObject.setMimeType("text/xml");
		oddRegistryObject.setName(new InternationalStringType());
		oddRegistryObject.getName().getLocalizedString().add(new LocalizedStringType());
		oddRegistryObject.getName().getLocalizedString().get(0).setValue(info.getTitle());
		
		// Get the earliest time something occurred and the latest
		Date lastEncounter = new Date(0), firstEncounter = new Date();
		
		if (info.getRelatedEncounter() != null)
			for (Obs el : info.getRelatedEncounter().getObs()) {
				if (el.getObsDatetime().before(firstEncounter))
					firstEncounter = el.getEncounter().getVisit().getStartDatetime();
				if (el.getObsDatetime().after(lastEncounter))
					lastEncounter = el.getEncounter().getVisit().getStopDatetime();
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
		    String.format("%s^^^^&%s&ISO", info.getPatient().getId().toString(), config.getPatientRoot()));
		InfosetUtil.addOrOverwriteSlot(
		    oddRegistryObject,
		    XDSConstants.SLOT_NAME_SOURCE_PATIENT_INFO,
		    String.format("PID-3|%s",
		        String.format("%s^^^^&%s&ISO", info.getPatient().getId().toString(), config.getPatientRoot())),
		    String.format("PID-5|%s^%s^^^", info.getPatient().getFamilyName(), info.getPatient().getGivenName()),
		    String.format("PID-7|%s", patientDob.getValue()), String.format("PID-8|%s", info.getPatient().getGender()));
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_LANGUAGE_CODE, Context.getLocale()
		        .toString());
		InfosetUtil.addOrOverwriteSlot(oddRegistryObject, XDSConstants.SLOT_NAME_CREATION_TIME, new SimpleDateFormat(
		        "yyyyMMddHHmmss").format(new Date()));
		
		// Unique identifier
		xdsUtil.addExtenalIdentifier(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_uniqueId,
		    String.format("2.25.%s", UUID.randomUUID().getLeastSignificantBits()), "XDSDocumentEntry.uniqueId");
		xdsUtil.addExtenalIdentifier(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_patientId,
		    xdsUtil.getPatientIdentifier(info.getPatient()), "XDSDocumentEntry.patientId");
		
		// Set classifications
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_classCode,
		    info.getClassCode(), "LOINC", "XDSDocumentEntry.classCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_confidentialityCode,
		    "1.3.6.1.4.1.21367.2006.7.101", "Connect-a-thon confidentialityCodes", "XDSDocumentEntry.confidentialityCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_formatCode,
		    info.getFormatCode(), "1.3.6.1.4.1.19376.1.2.3", "XDSDocumentEntry.formatCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject,
		    XDSConstants.UUID_XDSDocumentEntry_healthCareFacilityTypeCode, "Not Available",
		    "Connect-a-thon healthcareFacilityTypeCodes", "XDSDocumentEntry.healthCareFacilityTypeCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_practiceSettingCode,
		    "Not Available", "Connect-a-thon practiceSettingCodes", "UUID_XDSDocumentEntry.practiceSettingCode");
		xdsUtil.addCodedValueClassification(oddRegistryObject, XDSConstants.UUID_XDSDocumentEntry_typeCode,
		    info.getTypeCode(), "LOINC", "XDSDocumentEntry.typeCode");
		
		// Create the submission set
		TS now = TS.now();
		now.setDateValuePrecision(TS.SECONDNOTIMEZONE);
		
		RegistryPackageType regPackage = new RegistryPackageType();
		regPackage.setId("SubmissionSet01");
		InfosetUtil.addOrOverwriteSlot(regPackage, XDSConstants.SLOT_NAME_SUBMISSION_TIME, now.getValue());
		regPackage.setName(oddRegistryObject.getName());
		xdsUtil.addCodedValueClassification(regPackage, XDSConstants.UUID_XDSSubmissionSet_contentTypeCode,
		    info.getClassCode(), "LOINC", "XDSSubmissionSet.contentTypeCode");
		
		// Submission set external identifiers
		xdsUtil.addExtenalIdentifier(regPackage, XDSConstants.UUID_XDSSubmissionSet_uniqueId,
		    String.format("2.25.%s", UUID.randomUUID().getLeastSignificantBits()), "XDSSubmissionSet.uniqueId");
		xdsUtil.addExtenalIdentifier(regPackage, XDSConstants.UUID_XDSSubmissionSet_sourceId,
		    String.format("2.25.%s", UUID.randomUUID().getLeastSignificantBits()), "XDSSubmissionSet.sourceId");
		xdsUtil.addExtenalIdentifier(regPackage, XDSConstants.UUID_XDSSubmissionSet_patientId,
		    xdsUtil.getPatientIdentifier(info.getPatient()), "XDSSubmissionSet.patientId");
		
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
		association.setAssociationType("HasMember");
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
			authorClass.setId(String.format("Classification_%s", UUID.randomUUID().toString()));
			
			String authorText = String.format("%s^%s^%s^^^^^^&%s&ISO", pvdr.getId(), pvdr.getPerson().getFamilyName(), pvdr
			        .getPerson().getGivenName(), config.getProviderRoot());
			if (authors.contains(authorText))
				continue;
			else
				authors.add(authorText);
			
			InfosetUtil.addOrOverwriteSlot(authorClass, XDSConstants.SLOT_NAME_AUTHOR_PERSON, authorText);
			
			oddRegistryObject.getClassification().add(authorClass);
		}
		
		ProvideAndRegisterDocumentSetRequestType.Document doc = new ProvideAndRegisterDocumentSetRequestType.Document();
		doc.setId(oddRegistryObject.getId());
		doc.setValue(documentContent);
		retVal.getDocument().add(doc);
		
		return retVal;
	}
}
