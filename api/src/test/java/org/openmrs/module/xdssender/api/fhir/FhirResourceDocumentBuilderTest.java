package org.openmrs.module.xdssender.api.fhir;

import org.junit.Before;
import org.junit.Ignore;
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
    private static final String GLOBAL_CONFIGS = "global-configs.xml";

    @Before
    public void setUp() throws Exception {
        executeDataSet(DATASET);
        executeDataSet(GLOBAL_CONFIGS);
    }

    @Test
    @Ignore("See https://github.com/IsantePlus/openmrs-module-xds-sender/issues/64")
    public void testBuildDocument() {
        Patient patient = Context.getPatientService().getPatient(10);
        Encounter encounter = Context.getEncounterService().getEncounter(21);
        FhirResourceDocumentBuilder fhirResourceDocumentBuilder = Context.getService(FhirResourceDocumentBuilder.class);
        DocumentModel dm = fhirResourceDocumentBuilder.buildDocument(patient, encounter);
        assertNotNull(dm);
    }
}
