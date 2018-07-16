package org.openmrs.module.xdssender.api.xds;

import org.dcm4chee.xds2.common.XDSConstants;
import org.dcm4chee.xds2.infoset.rim.ClassificationType;
import org.dcm4chee.xds2.infoset.rim.ExtrinsicObjectType;
import org.dcm4chee.xds2.infoset.rim.VersionInfoType;
import org.dcm4chee.xds2.infoset.util.InfosetUtil;
import org.marc.everest.datatypes.TS;
import org.openmrs.Encounter;
import org.openmrs.LocationAttribute;
import org.openmrs.PatientIdentifier;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.model.DocumentInfo;

import javax.xml.bind.JAXBException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ExtrinsicObject extends ExtrinsicObjectType {

    protected String MIME_TYPE = "text/xml";

    protected String SOFTWARE_VERSION_COMMENT = "Software Version";

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    protected String patientId;

    protected String location;

    public ExtrinsicObject(String patientId, String location) {
        super();
        this.patientId = patientId;
        this.location = location;
    }

    public void setId(Encounter encounter) throws RuntimeException {
        if (encounter.getForm() == null) {
            throw new RuntimeException("Cannot send encounter without formId");
        }
        this.setId(encounter.getLocation().getUuid() + "/" + patientId + "/"
                + encounter.getEncounterType().getUuid() + "/" + encounter.getForm().getUuid() + "/"
                + DATE_FORMAT.format(encounter.getEncounterDatetime()));
        this.setMimeType(MIME_TYPE);
    }

    public void setContentVersionInfo(String version) {
        Module module = ModuleFactory.getModuleById(version);
        if (module != null) {
            VersionInfoType softwareVersion = new VersionInfoType();
            softwareVersion.setVersionName(module.getVersion());
            softwareVersion.setComment(SOFTWARE_VERSION_COMMENT);
            this.setContentVersionInfo(softwareVersion);
        }
    }

    public void addOrOverwriteSlot(TS firstEncounterTs, TS lastEncounterTs) throws JAXBException {
        firstEncounterTs.setDateValuePrecision(TS.MINUTENOTIMEZONE);
        lastEncounterTs.setDateValuePrecision(TS.MINUTENOTIMEZONE);
        InfosetUtil.addOrOverwriteSlot(this, XDSConstants.SLOT_NAME_SERVICE_START_TIME,
                firstEncounterTs.getValue());
        InfosetUtil.addOrOverwriteSlot(this, XDSConstants.SLOT_NAME_SERVICE_STOP_TIME,
                lastEncounterTs.getValue());

    }

    public void addSourcePatientInformation(DocumentInfo info, PatientIdentifier nationalIdentifier,
                                            PatientIdentifier siteIdentifier, XdsSenderConfig config,
                                            TS patientDob) throws JAXBException {
        InfosetUtil.addOrOverwriteSlot(this, XDSConstants.SLOT_NAME_SOURCE_PATIENT_ID,
                String.format("%s^^^&%s&ISO", patientId, config.getEcidRoot()));
        InfosetUtil.addOrOverwriteSlot(this, XDSConstants.SLOT_NAME_SOURCE_PATIENT_INFO,
                String.format("PID-3|%s^^^&%s&ISO", nationalIdentifier.getIdentifier(), config.getCodeNationalRoot()),
                String.format("PID-3|%s^^^&%s&ISO", siteIdentifier.getIdentifier(), config.getCodeStRoot()),
                String.format("PID-5|%s^%s^^^", info.getPatient().getFamilyName(), info.getPatient().getGivenName()),
                String.format("PID-7|%s", patientDob.getValue()), String.format("PID-8|%s", info.getPatient().getGender()),
                String.format("PID-11|%s", location));
        InfosetUtil.addOrOverwriteSlot(this, XDSConstants.SLOT_NAME_LANGUAGE_CODE, Context.getLocale()
                .toString());
        InfosetUtil.addOrOverwriteSlot(this, XDSConstants.SLOT_NAME_CREATION_TIME, new SimpleDateFormat(
                "yyyyMMddHHmmss").format(new Date()));
    }

    public void addAuthor(DocumentInfo info, XdsSenderConfig config) throws JAXBException {
        List<String> authors = new ArrayList<String>();

        for (Provider pvdr : info.getAuthors()) {
            ClassificationType authorClass = new ClassificationType();
            authorClass.setClassificationScheme(XDSConstants.UUID_XDSDocumentEntry_author);
            authorClass.setClassifiedObject(this.getId());
            authorClass.setNodeRepresentation("");
            authorClass.setId(String.format("Classification_%s", UUID.randomUUID().toString()));

            String authorText = String.format("%s^%s^%s^^^^^^&%s&ISO", pvdr.getIdentifier(), pvdr.getPerson()
                    .getFamilyName(), pvdr.getPerson().getGivenName(), config.getProviderRoot());
            if (authors.contains(authorText))
                continue;
            else
                authors.add(authorText);

            String siteCode = null;

            for (LocationAttribute attribute : info.getRelatedEncounter().getLocation().getAttributes()) {
                if (attribute.getAttributeType().getUuid().equals(XdsSenderConstants.LOCATION_SITECODE_ATTRIBUTE_UUID)) {
                    siteCode = attribute.getValue().toString();
                }
            }

            String institutionText = String.format("%s^^^^^&%s&ISO^^^^%s", info.getRelatedEncounter().getLocation()
                    .getName(), config.getLocationRoot(), siteCode);

            InfosetUtil.addOrOverwriteSlot(authorClass, XDSConstants.SLOT_NAME_AUTHOR_PERSON, authorText);
            InfosetUtil.addOrOverwriteSlot(authorClass, "authorInstitution", institutionText);
            this.getClassification().add(authorClass);
        }
    }
}
