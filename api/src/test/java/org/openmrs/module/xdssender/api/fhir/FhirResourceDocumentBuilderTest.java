package org.openmrs.module.xdssender.api.fhir;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = { "classpath*:moduleApplicationContext.xml", "classpath*:applicationContext-service.xml" }, inheritLocations = false)
public class FhirResourceDocumentBuilderTest extends BaseModuleContextSensitiveTest {

    private static final String DATASET = "lab-dataset.xml";

    @Before
    public void setUp() throws Exception {
        executeDataSet(DATASET);
    }

    @Test
    public void testBuildDocument() {
        Patient patient = Context.getPatientService().getPatient(10);
        Encounter encounter = Context.getEncounterService().getEncounter(21);
        FhirResourceDocumentBuilder fhirResourceDocumentBuilder = new FhirResourceDocumentBuilder();
        DocumentModel dm = fhirResourceDocumentBuilder.buildDocument(patient,encounter);
        assertNotNull(dm);
    }
}