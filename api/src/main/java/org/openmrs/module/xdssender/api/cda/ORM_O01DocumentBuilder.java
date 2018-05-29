package org.openmrs.module.xdssender.api.cda;

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

    private static final String ORM_O01 = "ORM_O01";

    @Autowired
    private OrderGeneratorManager orderGeneratorManager;

    public DocumentModel buildDocument(Encounter encounter) {
        try {
            String message = orderGeneratorManager.generateORMO01Message(encounter, OrderDestination.SCC);
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            return DocumentModel.createInstance(data, message);
        } catch (MessageCreationException ex) {
            log.error("Error generating orders:", ex);
        }

        return null;
    }

    public static String getDocId(String clinicalDocId) {
        return ORM_O01 + "-" + clinicalDocId;
    }
}
