package org.openmrs.module.xdssender.api.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Resource;
import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.TS;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Author;
import org.marc.everest.rmim.uv.cdar2.vocabulary.ContextControl;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.fhir2.api.translators.PatientTranslator;
import org.openmrs.module.fhir2.api.translators.impl.PatientTranslatorImpl;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.cda.CdaDataUtil;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.module.xdssender.api.fhir.exceptions.ResourceGenerationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import lombok.AccessLevel;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("fhirResourceDocumentBuilder")
@Setter(AccessLevel.PACKAGE)
public class FhirResourceDocumentBuilderImpl implements FhirResourceDocumentBuilder, ApplicationContextAware {

	private static final String FORMAT_CODE = "TEXT";

	private static final String CLASS_CODE = "34133-9";

	public static final String PROP_PID_LOCAL = "mpi-client.pid.local";

	public static final String IDENTIFIER_SYSTEM = "urn:ietf:rfc:3986";

	private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private CdaDataUtil cdaDataUtil;

    private Patient patient;

    @Autowired
    private PatientTranslator patientTranslator;

    private ApplicationContext applicationContext;

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getFormatCode() {
        return FORMAT_CODE;
    }

    public static String getClassCode() {
        return CLASS_CODE;
    }
    
    /**
     * @should return valid Patient FHIR resource 
     */
    public Resource generateFhirResource(Object openmrsEntity) throws ResourceGenerationException {
		Resource resource = null;
	    String localPatientId = "";

    	if (openmrsEntity instanceof Patient) {
    		Patient patient = (Patient)openmrsEntity;
    		resource = patientTranslator.toFhirResource(patient);
    		localPatientId = Context.getAdministrationService().getGlobalProperty(PROP_PID_LOCAL);
    		if (localPatientId == null) {
    			throw new ResourceGenerationException("Unable to retrieve the Local PID, ensure that the MPI client module is installed and the \"PID LOCAL\" global property has been set");
		    }
		    org.hl7.fhir.r4.model.Patient patientResource = (org.hl7.fhir.r4.model.Patient) resource;
		    patientResource.addIdentifier().setSystem(IDENTIFIER_SYSTEM).setValue(localPatientId + patient.getUuid());
			resource = patientResource;
	    } else {
    		log.error(String.format("Entity %s not yet implemented", openmrsEntity.getClass().getName()));
    		throw new ResourceGenerationException("Entity not implemented");
    	}
    	
		return resource;
    }

    /**
     * @should return valid document
     */
    @Override
	public DocumentModel buildDocument(Object openmrsEntity, Encounter encounter) {
        DocumentModel documentModel = null;
        try {
            log.debug("generating FHIR resource");
            Resource resource = generateFhirResource(openmrsEntity);
            String message = convertResourceToJson(resource);
            log.debug("Generated FHIR resource +++++ " + message);
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            List<Author> authors = getDocumentAuthors(encounter);

            documentModel = DocumentModel.createInstance(data, getClassCode(), XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, getFormatCode(),
                    message, authors);
        } catch (Exception e) {
        	log.error("Unable to build document", e);
			e.printStackTrace();
		}

        return documentModel;

	}

	private String convertResourceToJson(IBaseResource fhirResource) {
		return FhirContext.forR4().newJsonParser().encodeResourceToString(fhirResource);
	}
	
    private List<Author> getDocumentAuthors(Encounter encounter) {
        List<Author> authors = new ArrayList<>();
        for (Map.Entry<EncounterRole, Set<Provider>> encounterProvider : encounter.getProvidersByRoles().entrySet()) {
            for (Provider pvdr : encounterProvider.getValue()) {
                Author aut = new Author(ContextControl.OverridingPropagating);
                aut.setTime(new TS());
                aut.getTime().setNullFlavor(NullFlavor.NoInformation);
                aut.setAssignedAuthor(cdaDataUtil.createAuthorPerson(pvdr));
                authors.add(aut);
            }
        }
        return authors;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
