package org.openmrs.module.xdssender.api.service.impl;

import org.apache.commons.io.IOUtils;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.domain.dao.CcdDao;
import org.openmrs.module.xdssender.api.service.XdsImportService;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CcdServiceImplTest {

	public static final String JSON_PATIENT_NAME = "Lessai";

	public static final String MISSING_CCD_MESSAGE = "Unable to find or render CCD";

	@InjectMocks
	private CcdServiceImpl ccdService;
	
	@Mock
	private CcdDao ccdDao;
	
	@Mock
	private Ccd ccd;
	
	@Mock
	private XdsImportService xdsImportService;

	@Mock
	private ShrImportServiceImpl shrImportService;

	@Mock
	private Patient patient;

	@Mock
	private XdsSenderConfig config;

	@Captor
	private ArgumentCaptor<Patient> patientArgumentCaptor;

	@Test
	public void shouldSaveAndReturnDownloadedCcdFromShr() throws XDSException {
		when(ccdDao.saveOrUpdate(any(Ccd.class))).thenReturn(ccd);
		when(shrImportService.retrieveCCD(any(Patient.class))).thenReturn(ccd);
		when(config.getShrType()).thenReturn("fhir");

		Ccd result = ccdService.downloadAndSaveCcd(patient);

		assertEquals(ccd, result);
		verify(shrImportService).retrieveCCD(patientArgumentCaptor.capture());
		assertEquals(patient, patientArgumentCaptor.getValue());
		verify(ccdDao).saveOrUpdate(ccd);
	}

	@Test
	public void shouldReturnNullOnCcdNotFoundDuringDownload() throws XDSException {
		when(xdsImportService.retrieveCCD(any(Patient.class))).thenReturn(null);

		Ccd result = ccdService.downloadAndSaveCcd(patient);

		assertNull(result);
		verifyZeroInteractions(ccdDao);
	}
	
	@Test
	public void getLocallyStoredCcd_shouldFindCcdSavedLocally() {
		when(ccdDao.find(patient)).thenReturn(ccd);
		assertEquals(ccd, ccdService.getLocallyStoredCcd(patient));
	}

	@Test
	public void getHtmlParsedLocallyStoredCcd_shouldRenderPatientCcd() {
		when(ccdDao.find(patient)).thenReturn(ccd);

		String jsonBundle = getSamplePatientBundle();

		// TODO: Cleanup and load from file
		when(ccd.getDocument()).thenReturn(jsonBundle);

		// TODO Cleanup and use a constant
		assertThat(ccdService.getHtmlParsedLocallyStoredCcd(patient), containsString(JSON_PATIENT_NAME));
	}

	@Test
	public void getHtmlParsedLocallyStoredCcd_shouldRenderCcd() {
		String jsonBundle = getSamplePatientBundle();

		when(ccd.getDocument()).thenReturn(jsonBundle);

		assertThat(ccdService.getHtmlParsedLocallyStoredCcd(ccd), containsString(JSON_PATIENT_NAME));
	}


	@Test
	public void getHtmlParsedLocallyStoredCcd_shouldSaveAndReturnMissingCcdMessage() {
		when(ccd.getDocument()).thenReturn(null);

		assertThat(ccdService.getHtmlParsedLocallyStoredCcd(ccd), containsString(MISSING_CCD_MESSAGE));
	}

	@Test
	public void getHtmlParsedLocallyStoredCcd_shouldWorkForMissingPatient() {
		when(ccdDao.find(patient)).thenReturn(null);

		assertThat(ccdService.getHtmlParsedLocallyStoredCcd(patient), containsString(MISSING_CCD_MESSAGE));
	}

	private String getSamplePatientBundle() {
		String jsonBundle = null;
		try {
			jsonBundle = IOUtils.toString(CcdServiceImplTest.class.getClassLoader().getResourceAsStream("samplePatientBundle.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonBundle;
	}
}
