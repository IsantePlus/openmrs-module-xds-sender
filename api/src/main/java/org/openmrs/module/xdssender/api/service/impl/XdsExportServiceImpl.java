package org.openmrs.module.xdssender.api.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.RegistryResponseType;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.cda.ClinicalDocumentBuilder;
import org.openmrs.module.xdssender.api.cda.ORM_O01DocumentBuilder;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.module.xdssender.api.model.DocumentData;
import org.openmrs.module.xdssender.api.model.DocumentInfo;
import org.openmrs.module.xdssender.api.service.XdsExportService;
import org.openmrs.module.xdssender.api.xds.MessageUtil;
import org.openmrs.module.xdssender.api.xds.XdsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("xdsSender.XdsExportService")
public class XdsExportServiceImpl extends BaseOpenmrsService implements XdsExportService {

    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private ClinicalDocumentBuilder clinicalDocBuilder;

    @Autowired
    private ORM_O01DocumentBuilder ormDocBuilder;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private XdsSenderConfig config;

    @Autowired
    private XdsSender xdsSender;

    @Override
    public DocumentInfo exportProvideAndRegister(Encounter encounter, Patient patient) {
        try {
            DocumentModel clinicalDocModel = clinicalDocBuilder.buildDocument(patient, encounter);
            DocumentInfo clinicalDocInfo = new DocumentInfo(encounter, patient, clinicalDocModel,
                    "text/xsl", config.getProviderRoot());
            DocumentData clinicalDoc = new DocumentData(clinicalDocInfo, clinicalDocModel.getData());

            DocumentData msgDoc = null;
            DocumentModel msgDocModel = ormDocBuilder.buildDocument(encounter);
            if (msgDocModel != null) {
                DocumentInfo msgDocInfo = new DocumentInfo(encounter, patient, msgDocModel,
                        "text/plain", config.getProviderRoot());
                msgDoc = new DocumentData(msgDocInfo, msgDocModel.getData());
            }

            if (!messageUtil.getPatientIdentifier(clinicalDocInfo).getIdentifierType().getName().equals("ECID")) {
                throw new Exception("Patient doesn't have ECID Identifier.");
            }

            ProvideAndRegisterDocumentSetRequestType request = messageUtil.createProvideAndRegisterDocument(clinicalDoc,
                    msgDoc, encounter);
            RegistryResponseType response = xdsSender.sendProvideAndRegister(request);

            if (!response.getStatus().contains("Success"))
                throw new Exception("Could not execute provide and register");

            return clinicalDocInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
