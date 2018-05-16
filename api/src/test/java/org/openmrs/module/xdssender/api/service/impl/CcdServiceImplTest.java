package org.openmrs.module.xdssender.api.service.impl;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.domain.dao.CcdDao;
import org.openmrs.module.xdssender.api.service.XdsImportService;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CcdServiceImplTest {
	
	@InjectMocks
	private CcdServiceImpl ccdService;
	
	@Mock
	private CcdDao ccdDao;
	
	@Mock
	private Ccd ccd;
	
	@Mock
	private XdsImportService xdsImportService;
	
	@Mock
	private Patient patient;
	
	@Captor
	private ArgumentCaptor<Patient> patientArgumentCaptor;
	
	@Test
	public void shouldSaveAndReturnDownloadedCcd() throws XDSException {
		when(ccdDao.saveOrUpdate(any(Ccd.class))).thenReturn(ccd);
		when(xdsImportService.retrieveCCD(any(Patient.class))).thenReturn(ccd);
		
		Ccd result = ccdService.downloadAndSaveCcd(patient);
		
		assertEquals(ccd, result);
		verify(xdsImportService).retrieveCCD(patientArgumentCaptor.capture());
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
	public void shoudFindCcdSavedLocally() {
		when(ccdDao.find(patient)).thenReturn(ccd);
		assertEquals(ccd, ccdService.getLocallyStoredCcd(patient));
	}
}
