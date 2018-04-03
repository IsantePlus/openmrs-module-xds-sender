package org.openmrs.module.xdssender.api.domain.dao;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = { "classpath*:applicationContext-service.xml", "classpath*:moduleApplicationContext.xml" }, inheritLocations = false)
public class CcdDaoTest extends BaseModuleContextSensitiveTest {
	
	private static final String DOCUMENT = "<xml><ccd>Just a test</ccd></xml>";
	
	private static final String DATASET = "xds-dataset.xml";
	
	private static final int PATIENT_ID = 10;
	
	@Autowired
	private CcdDao ccdDao;
	
	@Autowired
	private PatientService patientService;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(DATASET);
	}
	
	@Test
	public void shouldSaveAndFindCcd() {
		Patient patient = patientService.getPatient(PATIENT_ID);
		
		Ccd ccd = new Ccd();
		ccd.setPatient(patient);
		ccd.setDocument(DOCUMENT);
		
		ccdDao.saveOrUpdate(ccd);
		
		Ccd found = ccdDao.find(patient);
		
		assertNotNull(found);
		assertEquals(DOCUMENT, found.getDocument());
		assertEquals(patient.getId(), found.getPatient().getId());
		assertNotNull(ccd.getDownloadDate());
	}
	
	@Test
	public void shouldUpdatePatientCcd() {
		Patient patient = patientService.getPatient(PATIENT_ID);
		
		Ccd ccd = new Ccd();
		ccd.setPatient(patient);
		ccd.setDocument("XXXXXXXX");
		
		ccdDao.saveOrUpdate(ccd);
		
		ccd = new Ccd();
		ccd.setPatient(patient);
		ccd.setDocument(DOCUMENT);
		
		ccdDao.saveOrUpdate(ccd);
		
		Ccd found = ccdDao.find(patient);
		
		assertNotNull(found);
		assertEquals(DOCUMENT, found.getDocument());
		assertEquals(patient.getId(), found.getPatient().getId());
	}
}
