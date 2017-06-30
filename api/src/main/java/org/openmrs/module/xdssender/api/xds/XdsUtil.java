package org.openmrs.module.xdssender.api.xds;

import org.dcm4chee.xds2.infoset.rim.ClassificationType;
import org.dcm4chee.xds2.infoset.rim.ExternalIdentifierType;
import org.dcm4chee.xds2.infoset.rim.InternationalStringType;
import org.dcm4chee.xds2.infoset.rim.LocalizedStringType;
import org.dcm4chee.xds2.infoset.rim.RegistryObjectType;
import org.dcm4chee.xds2.infoset.util.InfosetUtil;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;

@Component("xdssender.XdsUtil")
public final class XdsUtil {
	
	@Autowired
	private XdsSenderConfig config;
	
	/**
	 * Add external identifier
	 */
	public ExternalIdentifierType addExtenalIdentifier(final RegistryObjectType classifiedObj, final String uuid,
	        final String id, final String name) throws JAXBException {
		
		ExternalIdentifierType retVal = new ExternalIdentifierType();
		retVal.setRegistryObject(classifiedObj.getId());
		retVal.setIdentificationScheme(uuid);
		retVal.setValue(id);
		retVal.setName(new InternationalStringType());
		retVal.getName().getLocalizedString().add(new LocalizedStringType());
		retVal.getName().getLocalizedString().get(0).setValue(name);
		retVal.setId(String.format("eid%s", classifiedObj.getExternalIdentifier().size()));
		retVal.setName(new InternationalStringType());
		retVal.getName().getLocalizedString().add(new LocalizedStringType());
		retVal.getName().getLocalizedString().get(0).setValue(name);
		classifiedObj.getExternalIdentifier().add(retVal);
		return retVal;
	}
	
	/**
	 * Create a codified value classification
	 * 
	 * @throws JAXBException
	 */
	public ClassificationType addCodedValueClassification(final RegistryObjectType classifiedObj, final String uuid,
	        final String code, final String scheme, String name) throws JAXBException {
		ClassificationType retVal = new ClassificationType();
		retVal.setClassifiedObject(classifiedObj.getId());
		retVal.setClassificationScheme(uuid);
		retVal.setNodeRepresentation(code);
		retVal.setName(new InternationalStringType());
		retVal.getName().getLocalizedString().add(new LocalizedStringType());
		retVal.getName().getLocalizedString().get(0).setValue(code);
		retVal.setId(String.format("cl%s", retVal.hashCode()));
		InfosetUtil.addOrOverwriteSlot(retVal, "codingScheme", scheme);
		
		retVal.setName(new InternationalStringType());
		retVal.getName().getLocalizedString().add(new LocalizedStringType());
		retVal.getName().getLocalizedString().get(0).setValue(name);
		
		classifiedObj.getClassification().add(retVal);
		
		return retVal;
	}
	
	/**
	 * Format identifier for XDS meta-data
	 */
	private String formatId(String root, String extension) {
		return String.format("%s^^^&%s&ISO", extension, root);
	}
	
	/**
	 * Get the ECID identifier for the patient
	 */
	public String getPatientIdentifier(Patient patient) {
		for (PatientIdentifier pid : patient.getIdentifiers())
			if (pid.getIdentifierType().getName().equals(config.getEcidRoot())) // prefer the ecid
				return this.formatId(pid.getIdentifierType().getName(), pid.getIdentifier());
		return String.format(config.getPatientRoot(), patient.getId().toString());// use the local identifier as last effort!
	}
}
