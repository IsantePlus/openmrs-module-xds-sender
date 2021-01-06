package org.openmrs.module.xdssender.api.hl7;

import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.TS;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Author;
import org.marc.everest.rmim.uv.cdar2.vocabulary.ContextControl;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Provider;
import org.openmrs.module.labintegration.api.hl7.OrderGeneratorManager;
import org.openmrs.module.labintegration.api.hl7.messages.MessageCreationException;
import org.openmrs.module.labintegration.api.model.OrderDestination;
import org.openmrs.module.xdssender.api.cda.CdaDataUtil;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ORM_O01DocumentBuilder {

    private final Log log = LogFactory.getLog(this.getClass());

    private static final String FORMAT_CODE = "HL7/Lab 2.5";

    // @see https://wiki.ihe.net/index.php/XDS_classCode_Metadata_Coding_System
    private static final String CLASS_CODE = "34133-9";
    private static final String CLASS_CODE_SCHEME = "LOINC";

    @Autowired
    private CdaDataUtil cdaDataUtil;

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
        DocumentModel documentModel = null;
        if (OrderDestination.searchForExistence(encounter, OrderDestination.SCC)) {
            try {
                String message = orderGeneratorManager.generateORMO01Message(encounter, OrderDestination.SCC);
                byte[] data = message.getBytes(StandardCharsets.UTF_8);

                List<Author> authors = getDocumentAuthors(encounter);

                documentModel = DocumentModel.createInstance(data, getClassCode(), getClassCodeScheme(), getFormatCode(),
                        message, authors);
            } catch (MessageCreationException ex) {
                log.error("Error generating orders:", ex);
            }
        }

        return documentModel;
    }

    private List<Author> getDocumentAuthors(Encounter encounter) {
        List<Author> authors = new ArrayList<>();
        for (Map.Entry<EncounterRole, Set<Provider>> encounterProvider : encounter.getProvidersByRoles().entrySet()) {
            for (Provider pvdr : encounterProvider.getValue()) {
                Author aut = new Author(ContextControl.OverridingPropagating);
                aut.setTime(new TS());
                aut.getTime().setNullFlavor(NullFlavor.NoInformation);
                aut.setAssignedAuthor(cdaDataUtil.createAuthorPerson(pvdr));
                authors.add(aut);
            }
        }
        return authors;
    }
}
