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
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.module.fhir2.api.translators.PatientTranslator;
import org.openmrs.module.fhir2.api.translators.impl.PatientTranslatorImpl;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.cda.CdaDataUtil;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.module.xdssender.api.fhir.exceptions.ResourceGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FhirResourceDocumentBuilder {
    private static final String FORMAT_CODE = "TEXT";

	private static final String CLASS_CODE = "34133-9";

	private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private CdaDataUtil cdaDataUtil;

    private Patient patient;

    private PatientTranslator patientTranslator;

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

        patientTranslator = new PatientTranslatorImpl();
    	
    	if (openmrsEntity instanceof Patient) {
    		resource = patientTranslator.toFhirResource((Patient)openmrsEntity);
    	} else {
    		log.error(String.format("Entity %s not yet implemented", openmrsEntity.getClass().getName()));
    		throw new ResourceGenerationException("Entity not implemented");
    	}
    	
		return resource;
    }

    /**
     * @should return valid document
     */
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
}
