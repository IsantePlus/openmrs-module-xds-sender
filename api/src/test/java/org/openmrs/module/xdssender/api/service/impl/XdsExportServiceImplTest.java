package org.openmrs.module.xdssender.api.service.impl;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.module.xdssender.api.fhir.FhirResourceDocumentBuilder;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.openmrs.module.xdssender.api.service.XdsExportService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = { "classpath*:moduleApplicationContext.xml", "classpath*:applicationContext-service.xml" }, inheritLocations = false)
public class XdsExportServiceImplTest extends BaseModuleContextSensitiveTest {

	private static final String DATASET = "lab-dataset.xml";
	private static final String GLOBAL_CONFIGS = "global-configs.xml";

	@Before
	public void setUp() throws Exception {
		executeDataSet(DATASET);
		executeDataSet(GLOBAL_CONFIGS);
	}

	@Test
	public void testExportProvideAndRegister() {
		Patient patient = Context.getPatientService().getPatient(10);
		Encounter encounter = Context.getEncounterService().getEncounter(21);
		XdsExportService xdsExportService = Context.getService(XdsExportService.class);
		DocumentInfo di = xdsExportService.exportProvideAndRegister(encounter, patient);
		assertNotNull(di);
	}
}
