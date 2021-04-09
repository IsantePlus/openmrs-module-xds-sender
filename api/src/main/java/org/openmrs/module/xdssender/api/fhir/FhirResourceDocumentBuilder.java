package org.openmrs.module.xdssender.api.fhir;

import org.openmrs.Encounter;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.springframework.stereotype.Component;

@Component
public interface FhirResourceDocumentBuilder {
    DocumentModel buildDocument(Object openmrsEntity, Encounter encounter);
}
