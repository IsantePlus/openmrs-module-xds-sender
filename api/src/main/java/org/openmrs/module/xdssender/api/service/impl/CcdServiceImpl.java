package org.openmrs.module.xdssender.api.service.impl;

import ca.uhn.fhir.context.FhirContext;
import groovy.text.GStringTemplateEngine;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HumanName;
import org.openmrs.Patient;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.domain.dao.CcdDao;
import org.openmrs.module.xdssender.api.service.CcdService;
import org.openmrs.module.xdssender.api.service.XdsImportService;
import org.openmrs.module.xdssender.api.xds.XdsUtil;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service(value = "xdsSender.CcdService")
public class CcdServiceImpl implements CcdService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CcdServiceImpl.class);

    @Autowired
    @Qualifier("xdsSender.XdsImportService")
    private XdsImportService xdsImportService;

    @Autowired
    @Qualifier("xdsSender.ShrImportService")
    private ShrImportServiceImpl shrImportService;

    @Autowired
    private CcdDao ccdDao;

    @Autowired
    private XdsSenderConfig config;

    // Todo: Fix Autowiring

    // @Autowired
    // @Qualifier("fhirContext")
    private FhirContext fhirContext = FhirContext.forR4();

    @Override
    public Ccd getLocallyStoredCcd(Patient patient) {
        return ccdDao.find(patient);
    }

    @Override
    public String getHtmlParsedLocallyStoredCcd(Patient patient) {
        XdsUtil xdsUtil = new XdsUtil();
        String ccdString = getLocallyStoredCcd(patient).getDocument();
        Bundle resource = (Bundle) fhirContext.newJsonParser().parseResource(ccdString);
        File ccdTemplate = new File(OpenmrsUtil.getApplicationDataDirectory(), "ccdTemplate.txt");
        String ccdFormedString = null;
        try {
            if (!ccdTemplate.exists()) {
                ccdTemplate.createNewFile();
            }
            ccdFormedString = xdsUtil.parseCcdToHtml(resource, ccdTemplate);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ccdFormedString;
    }

    @Override
    public Ccd downloadAndSaveCcd(Patient patient) throws XDSException {
        Ccd ccd;

        if (true) {
            shrImportService.setFhirContext(fhirContext);
            ccd = shrImportService.retrieveCCD(patient);
        } else {
            ccd = xdsImportService.retrieveCCD(patient);
        }

        if (ccd != null) {
            ccd = ccdDao.saveOrUpdate(ccd);
        }

        return ccd;
    }

    @Override
    public void downloadCcdAsPDF(OutputStream stream, Patient patient) {
        LOGGER.info("CCD PDF is being downloaded.");
    }
}
