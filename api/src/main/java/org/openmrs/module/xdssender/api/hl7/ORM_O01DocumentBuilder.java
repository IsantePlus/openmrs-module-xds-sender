package org.openmrs.module.xdssender.api.hl7;

import org.openmrs.Encounter;
import org.openmrs.module.labintegration.api.hl7.OrderGeneratorManager;
import org.openmrs.module.labintegration.api.hl7.messages.MessageCreationException;
import org.openmrs.module.labintegration.api.model.OrderDestination;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;

@Component
public class ORM_O01DocumentBuilder {

    private final Log log = LogFactory.getLog(this.getClass());

    private static final String FORMAT_CODE = "HL7/Lab 2.5";

    // @see https://wiki.ihe.net/index.php/XDS_classCode_Metadata_Coding_System
    private static final String CLASS_CODE = "PRESCRIPTIONS";
    private static final String CLASS_CODE_SCHEME = "XDS Class Code";

    @Autowired
    private OrderGeneratorManager orderGeneratorManager;

    public String getFormatCode() {
        return FORMAT_CODE;
    }

    public static String getClassCode() {
        return CLASS_CODE;
    }

    public static String getClassCodeScheme() {
        return CLASS_CODE_SCHEME;
    }

    public DocumentModel buildDocument(Encounter encounter) {
        if (OrderDestination.searchForExistence(encounter, OrderDestination.SCC)) {
            try {
                String message = orderGeneratorManager.generateORMO01Message(encounter, OrderDestination.SCC);
                byte[] data = message.getBytes(StandardCharsets.UTF_8);

                return DocumentModel.createInstance(data, getClassCode(), getClassCodeScheme(), getFormatCode(), message);
            } catch (MessageCreationException ex) {
                log.error("Error generating orders:", ex);
            }
        }

        return null;
    }
}
