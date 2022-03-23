package org.openmrs.module.xdssender.api.xds;

import groovy.text.GStringTemplateEngine;
import org.dcm4chee.xds2.common.XDSConstants;
import org.dcm4chee.xds2.infoset.rim.ClassificationType;
import org.dcm4chee.xds2.infoset.rim.ExternalIdentifierType;
import org.dcm4chee.xds2.infoset.rim.InternationalStringType;
import org.dcm4chee.xds2.infoset.rim.LocalizedStringType;
import org.dcm4chee.xds2.infoset.rim.RegistryObjectType;
import org.dcm4chee.xds2.infoset.util.InfosetUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.fhir.exceptions.ResourceGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("xdssender.XdsUtil")
public final class XdsUtil {

    @Autowired
    private XdsSenderConfig config;

    private DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

    private static final Logger logger = LoggerFactory.getLogger(XdsUtil.class);

    public static PatientIdentifier getPlaceholderSystemIdentifier(Patient patient) throws Exception {
        PatientIdentifierType systemIdentifierType = new PatientIdentifierType();
        systemIdentifierType.setName(XdsSenderConstants.SYSTEM_IDENTIFIER_TYPE_NAME);
        systemIdentifierType.setUuid(XdsSenderConstants.SYSTEM_IDENTIFIER_TYPE_UUID);

        PatientIdentifier systemIdentifier = new PatientIdentifier();
        systemIdentifier.setIdentifierType(systemIdentifierType);
        String localPatientId = Context.getAdministrationService().getGlobalProperty(XdsSenderConstants.PROP_PID_LOCAL, "http://openmrs.org");
        if (localPatientId == null) {
            throw new Exception("Unable to retrieve the Local PID, ensure that the MPI client module is installed and the \"PID LOCAL\" global property has been set");
        }
        // systemIdentifier.setIdentifier(localPatientId + "/" + patient.getUuid());
        systemIdentifier.setIdentifier(patient.getUuid());

        return systemIdentifier;
    }

    public String parseCcdToHtml(Bundle resource, File ccdTemplate) throws IOException, ClassNotFoundException {
//        TODO Find a better way to filter the obs of interest
        List<String> codes = new ArrayList<String>() {{
            add("5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");//Diastolic BP
            add("8462-4");//Diastolic BP
            add("5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");//Systolic BP
            add("8480-6");//
            add("5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");//Height
            add("8302-2");
            add("5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");//Weight
            add("3141-9");
            add("5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");//Pulse
            add("8867-4");
            add("5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");//Temperature
            add("8310-5");
        }};

        Map<String, Object> ccdStringMap = new HashMap<>();
        ccdStringMap.put("createdDate", resource.getMeta().getLastUpdated().toString());
        List<Bundle.BundleEntryComponent> entry = resource.getEntry();
        List<VitalSign> vitalSigns = new ArrayList<>();
        List<CcdEncounter> encounters = new ArrayList<>();
        List<MedicationPrescription> medicationPrescriptions = new ArrayList<>();
        List<Medication> medications = new ArrayList<>();
        List<Immunization> immunizations = new ArrayList<>();
        List<ProcedureRequest> procedureRequests = new ArrayList<>();
        List<Procedure> procedures = new ArrayList<>();
        List<DiagnosticReport> diagnosticReports = new ArrayList<>();
        List<InsuranceCoverage> coverages = new ArrayList<>();
        List<Condition> conditions = new ArrayList<>();
        List<AllergyIntolerance> intolerances = new ArrayList<>();
//		process the resource entries individually and push them to the template

        for (Bundle.BundleEntryComponent bundleEntry : entry) {
            Resource eResource = bundleEntry.getResource();
            switch (eResource.getResourceType().name()) {
                case "Patient": {
                    mapPatientResource(ccdStringMap, (org.hl7.fhir.r4.model.Patient) eResource);
                    break;
                }
                case "Observation": {
//                    TODO - Rework all the mappings from Observations- limiting the fetch to height,weight,temp,pulse,BP(both systolic and diastolic)
                    Observation obs = (Observation) eResource;
                    if (isLabResult(obs)) {
                        DiagnosticReport diagnosticReport = mapDiagnosticReportResource(obs);
                        diagnosticReports.add(diagnosticReport);
                    } else if (codes.contains(obs.getCode().getCodingFirstRep().getCode())) {
                        vitalSigns.add(mapObservationResource(obs));
                    } else {
                        Date nextRefill = null;
//                        Process other obs
                        switch (obs.getCode().getCodingFirstRep().getCode()) {
                            case "984AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
//                                Process the immunizations
                                Immunization medication = mapImmunizationResource(obs);
                                immunizations.add(medication);
                                break;
                            }
                            case "1284AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA":
                            case "159614AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
                                Condition condition = mapConditionResource(obs);
                                conditions.add(condition);
                                break;
                            }
                            case "1442AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA":// Current Medication Order (Parent)
                            {
                                MedicationPrescription medicationRequest = mapMedicationRequest(obs);
                                medicationPrescriptions.add(medicationRequest);
                                break;
                            }
                            case "163711AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
                                // Current Medication Dispensed Construct
                                Medication medication = mapMedicationList(obs);
                                if(nextRefill != null) medication.setNextRefill(nextRefill);
                                medications.add(medication);
                                break;
                            }
                            case "162549AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
                                //                        process Date medication refills due
                                for (int i = 0; i < medications.size(); i++) {
                                    medications.get(i).setNextRefill(obs.getValueDateTimeType().getValue());
                                }
                                break;
                            }

                        }

                    }
                    break;
                }
                case "Encounter": {
                    CcdEncounter encounter = mapEncounterResource(ccdStringMap, (org.hl7.fhir.r4.model.Encounter) eResource);
                    encounters.add(encounter);
                    break;
                }
                case "AllergyIntolerance": {
                    AllergyIntolerance allergyIntolerance = mapAllergyIntoleranceResource((org.hl7.fhir.r4.model.AllergyIntolerance) eResource);
                    intolerances.add(allergyIntolerance);
                    break;
                }
                case "MedicationRequest": {
                    MedicationPrescription medicationRequest = mapPrescriptionResource((org.hl7.fhir.r4.model.MedicationRequest) eResource);
                    medicationPrescriptions.add(medicationRequest);
                    break;
                }
                case "Medication": {
                    Medication medication = mapMedicationRequest((org.hl7.fhir.r4.model.Medication) eResource);
                    medications.add(medication);
                    break;
                }
                case "Immunization": {
                    Immunization medication = mapImmunizationResource((org.hl7.fhir.r4.model.Immunization) eResource);
                    immunizations.add(medication);
                    break;
                }
                case "ProcedureRequest": {
//					ProcedureRequest procedureRequest = mapProcedureRequestResource(ccdStringMap, (org.hl7.fhir.r4.model.) eResource);
//					procedureRequests.add(procedureRequest);
//                    break;
                }
                case "Procedure": {
                    Procedure procedure = mapProcedureResource((org.hl7.fhir.r4.model.Procedure) eResource);
                    procedures.add(procedure);
                    break;
                }
                case "Condition": {
                    //Problems, Conditions, and Diagnoses
                    Condition condition = mapConditionResource((org.hl7.fhir.r4.model.Condition) eResource);
                    conditions.add(condition);

                    break;
                }
                case "DiagnosticReport": {
                    //findings and interpretation of diagnostic tests performed on patients
                    DiagnosticReport diagnosticReport = mapDiagnosticReportResource((org.hl7.fhir.r4.model.DiagnosticReport) eResource);
                    diagnosticReports.add(diagnosticReport);

                    break;
                }
                case "Coverage": {
                    //Financial instrument which may be used to reimburse or pay for health care products and services.
                    // Includes both insurance and self-payment.
                    InsuranceCoverage insuranceCoverage = mapCoverageResource((org.hl7.fhir.r4.model.Coverage) eResource);
                    coverages.add(insuranceCoverage);
                    break;
                }

            }

        }

        Collections.sort(vitalSigns, Collections.reverseOrder());
        Collections.sort(encounters, Collections.reverseOrder());
        Collections.sort(intolerances, Collections.reverseOrder());
        Collections.sort(medications, Collections.reverseOrder());
        Collections.sort(medicationPrescriptions, Collections.reverseOrder());
        Collections.sort(coverages, Collections.reverseOrder());
        Collections.sort(immunizations, Collections.reverseOrder());
        Collections.sort(procedures, Collections.reverseOrder());
        Collections.sort(conditions, Collections.reverseOrder());
        Collections.sort(diagnosticReports, Collections.reverseOrder());

        ccdStringMap.put("vitalSigns", vitalSigns);
        ccdStringMap.put("encounters", encounters);
        ccdStringMap.put("intolerances", intolerances);
        ccdStringMap.put("medications", medications);
        ccdStringMap.put("medicationPrescriptions", medicationPrescriptions);
        ccdStringMap.put("coverages", coverages);
        ccdStringMap.put("immunizations", immunizations);
        ccdStringMap.put("procedures", procedures);
        ccdStringMap.put("conditions", conditions);
        ccdStringMap.put("diagnosticReports", diagnosticReports);
        GStringTemplateEngine templateEngine = new GStringTemplateEngine();
        String htmlString;
        try {
            htmlString = templateEngine.createTemplate(ccdTemplate).make(ccdStringMap).toString();
        } catch (Exception e) {
            logger.error("Could not parse HTML!\n" + e.toString());
            htmlString = "<h2>CCD was not rendered correctly</h2>";
        }

        return htmlString;
    }

    private boolean isLabResult(Observation obs) {
        boolean isResult = false;
        for (int i = 0; i < obs.getCategory().size(); i++) {
            CodeableConcept codeableConcept = obs.getCategory().get(i);
            for (int j = 0; j < codeableConcept.getCoding().size(); j++) {
                if (codeableConcept.getCoding().get(j).getCode().equalsIgnoreCase("laboratory")) {
                    isResult = true;
                    break;
                }
            }
            if (isResult) {
                break;
            }
        }
        return isResult;
    }


    private MedicationPrescription mapMedicationRequest(Observation obs) {
        // String identifier, String status, String intent, String category, String authoredOn, String requester, String reasonCode, String dosage
        return new MedicationPrescription(obs.getValue().toString(),
                obs.getStatus().getDisplay(),
                "",
                obs.hasCode() ? obs.getCode().getCodingFirstRep().getDisplay() : "",
                obs.getEffectiveDateTimeType().getValue(),
                ((Encounter) obs.getEncounter().getResource()).getParticipantFirstRep().getIndividual().getDisplay(),
                "",
                obs.getValue().toString(),
                ((Encounter) obs.getEncounter().getResource()).getLocationFirstRep().getLocation().getDisplay());
    }

    private String getMedName(Observation obs) {
        try {
            return obs.hasHasMember() && obs.getHasMember().size() > 1 ?
                    ((Observation) ((Reference) obs.getHasMember().toArray()[1]).getResource()).getValueCodeableConcept().getCodingFirstRep().getDisplay() :
                    obs.getValue().toString();
        } catch (Exception e) {
            return obs.getValue().toString();
        }
    }

    private Medication mapMedicationList(Observation obs) {
        Medication medication = new Medication(
                obs.getValue().toString(),
                getMedName(obs),
                obs.getIssued(),
                "", "", "", "", "",
                obs.getStatus().getDisplay(), "", "",
                obs.getValue().toString(),
                ((Encounter) obs.getEncounter().getResource()).getLocationFirstRep().getLocation().getDisplay(),
                null, 0, "", 0);
//        TODO - Should we use current
        if (obs.hasHasMember()) {
            List<Reference> members = obs.getHasMember();
            for (Reference member : members) {
//                Process the members
//Medication orders  1282AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//Current medication dispensed construct  163711AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//Medication Duration 159368AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//Medication strength  1444AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//Medication dispensed 1443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (Number Dispensed)
//Indication for medication  160742AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (Disease)
//MEDICATION RECEIVED AT VISIT  1276AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//Date medication refills due  162549AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                Observation memberResource = (Observation) member.getResource();
                logger.error(memberResource.getId());
                logger.error(memberResource.getCode().getCodingFirstRep().getDisplay());
                logger.error(memberResource.getCode().getCodingFirstRep().getCode());
                switch (memberResource.getCode().getCodingFirstRep().getCode()) {
                    case "1282AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
//                        process medication order
                        medication.setMedication(memberResource.getValueCodeableConcept().getCodingFirstRep().getDisplay());
                        break;
                    }
                    case "159368AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
//                        process Medication duration
                        medication.setNumberOfDays(memberResource.getValueQuantity().getValue().intValue());
                        break;
                    }
                    case "1444AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
//                        process Medication strength
                        medication.setStrength(memberResource.getValue().toString());
                        break;
                    }
                    case "1443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
//                        process Medication dispensed(number dispensed)
                        medication.setNumberDispensed(memberResource.getValueQuantity().getValue().intValue());
                        break;
                    }
                    case "160742AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
//                        process Indication for medication
                        medication.setIndications(memberResource.getValueCodeableConcept().getCodingFirstRep().getDisplay());
                        break;
                    }
                }


            }
        }
        return medication;
    }

    private Immunization mapImmunizationResource(Observation obs) {
//        identifier, vaccineCode,doseQuantity, occurrenceDate,site,route,status,
        String identifier = null;
        identifier = getLoincCode(obs.getCode());

        return new Immunization(
                obs.getValueCodeableConcept().getCodingFirstRep().getDisplay()
                , obs.getCode().getCodingFirstRep().getCode()
                , ""
                , obs.getEffectiveDateTimeType().getValue(),
                ((Encounter) obs.getEncounter().getResource()).getLocationFirstRep().getLocation().getDisplay(),
                "", "",
                obs.getValueCodeableConcept().getCodingFirstRep().getDisplay());
    }

    private InsuranceCoverage mapCoverageResource(Coverage coverage) {
//        payer,type,policyId,policyHolder,coveredParty,relationship,planInformation
        return new InsuranceCoverage(coverage.getPayorFirstRep().getDisplay(),
                coverage.getType().getText(),
                coverage.getIdentifierFirstRep().getValue(),
                coverage.getPolicyHolder().getDisplay(),
                coverage.getBeneficiary().getDisplay(),
                coverage.getRelationship().getText(),
                coverage.getContractFirstRep().getDisplay());
    }

    private Procedure mapProcedureResource(org.hl7.fhir.r4.model.Procedure procedure) {
        return new Procedure(procedure);
    }

    private DiagnosticReport mapDiagnosticReportResource(org.hl7.fhir.r4.model.DiagnosticReport diagnosticReport) {
        return new DiagnosticReport(diagnosticReport);
    }

    private DiagnosticReport mapDiagnosticReportResource(Observation obs) {
//        identifier, category, name, date, result, status, conclusion, presentedForm

        String result = "";
        if (obs.getValue() instanceof Quantity) {
            result = obs.getValueQuantity().getValue().toPlainString();
        } else if (obs.getValue() instanceof CodeableConcept) {
            result = obs.getValueCodeableConcept().getCodingFirstRep().getDisplay();
        }
        return new DiagnosticReport(
                getLoincCode(obs.getCode()),
                obs.getCategoryFirstRep().getCodingFirstRep().getDisplay(),
                obs.getCode().getCodingFirstRep().getDisplay(),
                obs.getEffectiveDateTimeType().getValue(),
                result, obs.getStatus().getDisplay(), "", "", getLocationFromPatient(obs.getSubject()));
    }

    private Condition mapConditionResource(org.hl7.fhir.r4.model.Condition condition) {
        return new Condition(condition);
    }

    private Condition mapConditionResource(Observation obs) {
//        code, displayName, description, type, effectiveDates, severity, notes
        String identifier = null;
        identifier = getLoincCode(obs.getCode());
        Coding codingFirstRep = obs.getCode().getCodingFirstRep();
        return new Condition(identifier != null ? identifier : codingFirstRep.getCode(),
                obs.getValueCodeableConcept().getCodingFirstRep().getDisplay(),
                codingFirstRep.getDisplay(),
                codingFirstRep.getDisplay(), obs.getIssued(), "", "",
                getLocationFromPatient(obs.getSubject()));
    }

    private String getLoincCode(CodeableConcept cc) {
        for (Coding coding : cc.getCoding()) {
            if ("http://loinc.org".equals(coding.getSystem())) {
                return coding.getCode();
            }
        }

        return cc.getCodingFirstRep().getCode();
    }

    private ProcedureRequest mapProcedureRequestResource(org.hl7.fhir.r4.model.Procedure procedure) {
        //    	TODO - Finish the mappings
        return new ProcedureRequest();
    }

    private Immunization mapImmunizationResource(org.hl7.fhir.r4.model.Immunization immunization) {
        return new Immunization(immunization);
    }

    private Medication mapMedicationRequest(org.hl7.fhir.r4.model.Medication medication) {
        return new Medication(medication);
    }

    private MedicationPrescription mapPrescriptionResource(MedicationRequest medicationRequest) {
        return new MedicationPrescription(medicationRequest);
    }

    private AllergyIntolerance mapAllergyIntoleranceResource(org.hl7.fhir.r4.model.AllergyIntolerance intolerance) {
        org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent reactionFirstRep = intolerance.getReactionFirstRep();

        String location = getLocationFromPatient(intolerance.getPatient());
        Date recordedDate = intolerance.hasRecordedDate() ? intolerance.getRecordedDate() : null;

        return new AllergyIntolerance(intolerance.getType().getDisplay() + (intolerance.hasCategory() ? " - " + intolerance.getCategory().get(0).getCode() : ""),
                intolerance.getCode().getCodingFirstRep().getDisplay(),
                reactionFirstRep.getSubstance() != null ? reactionFirstRep.getSubstance().getCodingFirstRep().getDisplay() : "",
                reactionFirstRep.getManifestationFirstRep() != null ? reactionFirstRep.getManifestationFirstRep().getCodingFirstRep().getDisplay() : "",
                intolerance.getClinicalStatus() != null ? intolerance.getClinicalStatus().getText() : "",
                reactionFirstRep.getSeverity() != null ? reactionFirstRep.getSeverity().getDisplay() : "",
                intolerance.getMeta().getSource(), location, recordedDate
        );
    }

    private String getLocationFromPatient(Reference patientRef) {
        org.hl7.fhir.r4.model.Patient patient = (org.hl7.fhir.r4.model.Patient) patientRef.getResource();

        if (patient != null && patient.hasIdentifier()) {
            try {
                return ((Reference) patient.getIdentifierFirstRep().getExtensionFirstRep().getValue()).getDisplay();
            } catch (Exception e) {
                // Ignore for now
            }
        }

        return "";
    }

    private CcdEncounter mapEncounterResource(Map<String, Object> ccdStringMap, Encounter encounter) {
//    	encounter, providers, location, date, indications, dataSource, type
        String displayDesc = "";
        if (encounter.getTypeFirstRep().hasCoding()) {
            displayDesc = encounter.getTypeFirstRep().getCodingFirstRep().getDisplay();
        } else if (encounter.hasClass_()) {
            displayDesc = encounter.getClass_().getCode();
        }

        Period period = encounter.getPeriod();
        Date encounterPeriod = null;
        if (period != null) {
            encounterPeriod = period.getStart();
        }
        return new CcdEncounter(encounter.toString(),
                encounter.getParticipantFirstRep().getIndividual().getDisplay(),
                encounter.getLocationFirstRep().getLocation().getDisplay(),
                encounterPeriod,
                null,
                encounter.getMeta().getSource(),
                displayDesc);
    }

    private VitalSign mapObservationResource(Observation obs) {

//		id, name, value, range, interpretationCode, description, date, dataSource
        String identifier = null;
        String name = null;
        for (Coding coding : obs.getCode().getCoding()) {
            if ("http://loinc.org".equals(coding.getSystem())) {
                identifier = coding.getCode();
                name = coding.getDisplay();
            }
        }
        return new VitalSign(
                identifier != null ? identifier : obs.getCode().getCodingFirstRep().getCode()
                , name != null ? name : obs.getCode().getCodingFirstRep().getDisplay()
                , obs.getValueQuantity().getValue().toString()
                , obs.getReferenceRangeFirstRep().getText()
                , obs.getCode().getCodingFirstRep().getCode()
                , obs.getCode().getCodingFirstRep().getDisplay()
//                TODO switch to fetching the source details from the encounter
                , obs.getIssued(), ((Encounter) obs.getEncounter().getResource()).getLocationFirstRep().getLocation().getDisplay());
    }

    private static void mapPatientResource(Map<String, Object> ccdStringMap, org.hl7.fhir.r4.model.Patient pat) {
        //					parse patient resource
        XdsUtil util = new XdsUtil();
        HumanName patientName = pat.getNameFirstRep();
        Date birthDate = pat.getBirthDate();
        String gender = pat.getGender() != null ? pat.getGender().getDisplay() : "";
        Address addressFirstRep = pat.getAddressFirstRep();
        org.hl7.fhir.r4.model.Patient.ContactComponent patientContacts = pat.getContactFirstRep();
        Reference patientGeneralPractitioner = pat.getGeneralPractitionerFirstRep();
        Identifier patientId = pat.getIdentifierFirstRep();
        CodeableConcept maritalStatus = pat.getMaritalStatus();
        ContactPoint telephone = pat.getTelecomFirstRep();
        putValue(ccdStringMap, "familyName", patientName.getFamily());
        putValue(ccdStringMap, "givenName", patientName.getGiven().toString().replace("[", "").replace("]", ""));
        putValue(ccdStringMap, "birthDate", util.formatDate(birthDate));
        putValue(ccdStringMap, "gender", gender);
        putValue(ccdStringMap, "address", addressFirstRep);

        putValue(ccdStringMap, "patientId", patientId.getValue());
        putValue(ccdStringMap, "maritalStatus", maritalStatus.getText());
        putValue(ccdStringMap, "telephone", telephone.getValue());
        putValue(ccdStringMap, "patientGeneralPractitioner", patientGeneralPractitioner.getDisplay());
//        Not currently returned
        putValue(ccdStringMap, "race", "");
        putValue(ccdStringMap, "language", "");
        putValue(ccdStringMap, "ethnicity", "");
        putValue(ccdStringMap, "guardian", "");

    }

    private static void putValue(Map<String, Object> ccdStringMap, String key, Object value) {
        try {
            if (value != null &&
                    (!ccdStringMap.containsKey(key)
                            || ccdStringMap.get(key) == null
                            || ((String) ccdStringMap.get(key)).isEmpty())) {
                ccdStringMap.put(key, value);
            } else {
                if(!ccdStringMap.containsKey(key)) {
                    ccdStringMap.put(key,"");
                }
            }
            return;
        } catch (Exception e) {
            if(!ccdStringMap.containsKey(key)) {
                ccdStringMap.put(key,"");
            }
        } finally {
            return;
        }
    }

    /**
     * Add external identifier
     */
    public ExternalIdentifierType addExtenalIdentifier(final RegistryObjectType classifiedObj, final String uuid,
                                                       final String id, final String name) throws JAXBException {

        ExternalIdentifierType retVal = new ExternalIdentifierType();
        retVal.setRegistryObject(classifiedObj.getId());
        retVal.setIdentificationScheme(uuid);
        retVal.setValue(id);
        retVal.setName(new InternationalStringType());
        retVal.getName().getLocalizedString().add(new LocalizedStringType());
        retVal.getName().getLocalizedString().get(0).setValue(name);
        retVal.setId(String.format("eid%s", classifiedObj.getExternalIdentifier().size()));
        retVal.setName(new InternationalStringType());
        retVal.getName().getLocalizedString().add(new LocalizedStringType());
        retVal.getName().getLocalizedString().get(0).setValue(name);
        classifiedObj.getExternalIdentifier().add(retVal);
        return retVal;
    }

    /**
     * Create a codified value classification
     *
     * @throws JAXBException
     */
    public ClassificationType addCodedValueClassification(final RegistryObjectType classifiedObj, final String uuid,
                                                          final String code, final String scheme, String name) throws JAXBException {
        ClassificationType retVal = new ClassificationType();
        retVal.setClassifiedObject(classifiedObj.getId());
        retVal.setClassificationScheme(uuid);
        retVal.setNodeRepresentation(code);
        retVal.setName(new InternationalStringType());
        retVal.getName().getLocalizedString().add(new LocalizedStringType());
        retVal.getName().getLocalizedString().get(0).setValue(code);
        retVal.setId(String.format("cl%s", retVal.hashCode()));
        InfosetUtil.addOrOverwriteSlot(retVal, "codingScheme", scheme);

        retVal.setName(new InternationalStringType());
        retVal.getName().getLocalizedString().add(new LocalizedStringType());
        retVal.getName().getLocalizedString().get(0).setValue(name);

        classifiedObj.getClassification().add(retVal);

        return retVal;
    }

    /**
     * Format identifier for XDS meta-data
     */
    private String formatId(String root, String extension) {
        return String.format("%s^^^&%s&ISO", extension, root);
    }

    /**
     * Get the ECID identifier for the patient
     */
    public String getPatientIdentifier(Patient patient) {
        for (PatientIdentifier pid : patient.getIdentifiers())
            if (pid.getIdentifierType().getName().equals(config.getEcidRoot())) // prefer the ecid
                return this.formatId(pid.getIdentifierType().getName(), pid.getIdentifier());
        return String.format(config.getPatientRoot(), patient.getId().toString());// use the local identifier as last effort!
    }

    /**
     * Compare Dates for Sorting
     */
    public int compareDates(Date d1, Date d2) {
        if (d1 == null) {
            return (d2 == null ? 0 : -1);
        }

        // d1 can't be null here:
        if (d2 == null) {
            return 1;
        }

        return d1.compareTo(d2);
    }

    private class VitalSign implements Comparable<VitalSign> {
        private String id, name, value, range, interpretationCode, description, location;
        private Date date;

        public VitalSign(String id, String name, String value, String range, String interpretationCode, String description, Date date, String location) {
            this.id = id;
            this.name = name;
            this.value = value;
            this.range = range;
            this.interpretationCode = interpretationCode;
            this.description = description;
            this.date = date;
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }

        public String getInterpretationCode() {
            return interpretationCode;
        }

        public void setInterpretationCode(String interpretationCode) {
            this.interpretationCode = interpretationCode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getDate() {
            return date;
        }

        public String getFormattedDate() {
            return formatDate(date);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public int compareTo(VitalSign o) {
            return compareDates(getDate(), o.getDate());
        }


    }

    private class CcdEncounter implements Comparable<CcdEncounter> {
        private String encounter, providers, location, indications, dataSource, type;
        private Date date;

        public CcdEncounter(String encounter, String providers, String location, Date date, String indications, String dataSource, String type) {
            this.encounter = encounter;
            this.providers = providers;
            this.location = location;
            this.type = type;
            this.date = date;
            this.indications = indications;
            this.dataSource = dataSource;
        }

        public String getEncounter() {
            return encounter;
        }

        public void setEncounter(String encounter) {
            this.encounter = encounter;
        }

        public String getProviders() {
            return providers != null ? providers : "";
        }

        public void setProviders(String providers) {
            this.providers = providers;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getFormattedDate() {
            return formatDate(this.getDate());
        }

        public String getIndications() {
            return indications;
        }

        public void setIndications(String indications) {
            this.indications = indications;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public int compareTo(CcdEncounter e) {
            int r;
            try {
                r = getDate() == null ?
                        (e.getDate() == null ? 0 : -1)
                        : getDate().compareTo(e.getDate());
                return r;
            } catch (Exception ex) {
                return 0;
            }
        }
    }

    private class MedicationPrescription implements Comparable<MedicationPrescription> {
        private String identifier, status, intent, category, requester, reasonCode, dosage, location;
        private Date authoredOn;

        public MedicationPrescription(MedicationRequest medicationRequest) {
        }

        public MedicationPrescription(String identifier, String status, String intent, String category, Date authoredOn,
                                      String requester, String reasonCode, String dosage, String location) {
            this.identifier = identifier;
            this.status = status;
            this.intent = intent;
            this.category = category;
            this.authoredOn = authoredOn;
            this.requester = requester;
            this.reasonCode = reasonCode;
            this.dosage = dosage;
            this.location = location;
        }

        public String getIdentifier() {
            String[] split = identifier.split(",");
            String filtered = "";
            for (int i = 0; i < split.length; i++) {
                if (!(split[i].toLowerCase().contains("human") || split[i].matches(".*\\d.*"))) {
                    filtered = split[i];
                }
            }
            return filtered;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Date getAuthoredOn() {
            return authoredOn;
        }

        public String getFormattedDate() {
            return formatDate(this.authoredOn);
        }

        public void setAuthoredOn(Date authoredOn) {
            this.authoredOn = authoredOn;
        }

        public String getRequester() {
            return requester != null ? requester : "";
        }

        public void setRequester(String requester) {
            this.requester = requester;
        }

        public String getReasonCode() {
            return reasonCode;
        }

        public void setReasonCode(String reasonCode) {
            this.reasonCode = reasonCode;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public int compareTo(MedicationPrescription o) {
            return getAuthoredOn().compareTo(o.getAuthoredOn());
        }

    }

    private class Medication implements Comparable<Medication> {
        private String medication, brandName, productForm, dose, route, adminInstructions, pharmInstructions,
                status, indications, reaction, description, dataSource, strength;
        //        Indication for medication  160742AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (Disease)
        private Date startDate, nextRefill; //Date medication refills due  162549AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        private int numberOfDays, numberDispensed; //Medication dispensed 1443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA (Number Dispensed)

        public Medication(org.hl7.fhir.r4.model.Medication medication) {
        }

        public Medication(String medication, String brandName, Date startDate, String productForm, String dose,
                          String route, String adminInstructions, String pharmInstructions, String status, String indications,
                          String reaction, String description, String dataSource, Date nextRefill, int numberOfDays,
                          String strength, int numberDispensed) {
            this.medication = medication;
            this.brandName = brandName;
            this.startDate = startDate;
            this.productForm = productForm;
            this.dose = dose;
            this.route = route;
            this.adminInstructions = adminInstructions;
            this.pharmInstructions = pharmInstructions;
            this.status = status;
            this.indications = indications;
            this.reaction = reaction;
            this.description = description;
            this.dataSource = dataSource;
            this.nextRefill = nextRefill;
            this.numberOfDays = numberOfDays;
            this.strength = strength;
            this.numberDispensed = numberDispensed;
        }

        public int getNumberDispensed() {
            return numberDispensed;
        }

        public void setNumberDispensed(int numberDispensed) {
            this.numberDispensed = numberDispensed;
        }

        public String getMedication() {
            return medication;
        }

        public void setMedication(String medication) {
            this.medication = medication;
        }

        public String getBrandName() {
            String[] split = brandName.split(",");
            String filtered = "";
            for (int i = 0; i < split.length; i++) {
                if (!(split[i].toLowerCase().contains("true") || split[i].matches(".*\\d.*"))) {
                    filtered = split[i];
                }
            }
            return filtered;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public Date getStartDate() {
            return startDate;
        }

        public String getFormattedDate() {
            return formatDate(startDate);
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public String getProductForm() {
            return productForm;
        }

        public void setProductForm(String productForm) {
            this.productForm = productForm;
        }

        public String getDose() {
            return dose;
        }

        public void setDose(String dose) {
            this.dose = dose;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public String getAdminInstructions() {
            return adminInstructions;
        }

        public void setAdminInstructions(String adminInstructions) {
            this.adminInstructions = adminInstructions;
        }

        public String getPharmInstructions() {
            return pharmInstructions;
        }

        public void setPharmInstructions(String pharmInstructions) {
            this.pharmInstructions = pharmInstructions;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIndications() {
            return indications;
        }

        public void setIndications(String indications) {
            this.indications = indications;
        }

        public String getReaction() {
            return reaction;
        }

        public void setReaction(String reaction) {
            this.reaction = reaction;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getNextRefill() {
            return formatDate(nextRefill);
        }

        public void setNextRefill(Date nextRefill) {
            this.nextRefill = nextRefill;
        }

        public int getNumberOfDays() {
            return numberOfDays;
        }

        public void setNumberOfDays(int numberOfDays) {
            this.numberOfDays = numberOfDays;
        }

        @Override
        public int compareTo(Medication o) {
            return getStartDate().compareTo(o.getStartDate());
        }

        public String getStrength() {
            return strength;
        }

        public void setStrength(String strength) {
            this.strength = strength;
        }
    }

    private class AllergyIntolerance implements Comparable<AllergyIntolerance> {
        private String type, description, substance, reaction, status, criticality, dataSource, location;
        private Date date;

        public AllergyIntolerance(String type, String description, String substance,
                                  String reaction, String status, String criticality,
                                  String dataSource, String location, Date date) {
            this.type = type;
            this.description = description;
            this.substance = substance;
            this.reaction = reaction;
            this.status = status;
            this.criticality = criticality;
            this.dataSource = dataSource;
            this.location = location;
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSubstance() {
            return substance;
        }

        public void setSubstance(String substance) {
            this.substance = substance;
        }

        public String getReaction() {
            return reaction;
        }

        public void setReaction(String reaction) {
            this.reaction = reaction;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getCriticality() {
            return criticality;
        }

        public void setCriticality(String criticality) {
            this.criticality = criticality;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public int compareTo(AllergyIntolerance o) {
            return getDate().compareTo(o.getDate());
        }

    }

    private class Immunization implements Comparable<Immunization> {
        private String identifier, vaccineCode, doseQuantity, site, route, status, notes;
        private Date occurrenceDate;

        public Immunization(String identifier, String vaccineCode, String doseQuantity, Date occurrenceDate,
                            String site, String route, String status, String notes) {
            this.identifier = identifier;
            this.vaccineCode = vaccineCode;
            this.doseQuantity = doseQuantity;
            this.occurrenceDate = occurrenceDate;
            this.site = site;
            this.route = route;
            this.status = status;
            this.notes = notes;
        }

        public Immunization(org.hl7.fhir.r4.model.Immunization immunization) {
            setIdentifier(immunization.getIdentifierFirstRep().getValue());
            setVaccineCode(immunization.getVaccineCode().getText());
            setDoseQuantity(immunization.getDoseQuantity().getValue().toString());
            setOccurrenceDate(immunization.getOccurrenceDateTimeType().getValue());
            setSite(immunization.getSite().getCodingFirstRep().getDisplay());
            setRoute(immunization.getRoute().getCodingFirstRep().getDisplay());
            setStatus(immunization.getStatus().getDisplay());
            setNotes(immunization.getNoteFirstRep().getText());

        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getVaccineCode() {
            return vaccineCode;
        }

        public void setVaccineCode(String vaccineCode) {
            this.vaccineCode = vaccineCode;
        }

        public Date getOccurrenceDate() {
            return occurrenceDate;
        }

        public void setOccurrenceDate(Date occurrenceDate) {
            this.occurrenceDate = occurrenceDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public String getDoseQuantity() {
            return doseQuantity;
        }

        public void setDoseQuantity(String doseQuantity) {
            this.doseQuantity = doseQuantity;
        }

        @Override
        public int compareTo(Immunization o) {
            return getOccurrenceDate().compareTo(o.getOccurrenceDate());
        }


    }

    private class ProcedureRequest {

    }

    private class Procedure implements Comparable<Procedure> {
        private String code, procedure, description, indications, outcome, location;
        private Date date;

        public Procedure(String code, String procedure, String description, Date date,
                         String indications, String outcome, String location) {
            this.code = code;
            this.procedure = procedure;
            this.description = description;
            this.date = date;
            this.indications = indications;
            this.outcome = outcome;
            this.location = location;
        }

        public Procedure(org.hl7.fhir.r4.model.Procedure procedure) {
            setCode(procedure.getCode().getCodingFirstRep().getCode());
            setProcedure(procedure.getFocalDeviceFirstRep().getAction().getCodingFirstRep().getDisplay());
            setDescription(procedure.getFocalDeviceFirstRep().getManipulated().getDisplay());
            setDate(procedure.getPerformedDateTimeType().getValue());
            setIndications(procedure.getCategory().getText());
            setOutcome(procedure.getOutcome().getText());
            setLocation(getLocationFromPatient(procedure.getSubject()));
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getProcedure() {
            return procedure;
        }

        public void setProcedure(String procedure) {
            this.procedure = procedure;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getIndications() {
            return indications;
        }

        public void setIndications(String indications) {
            this.indications = indications;
        }

        public String getOutcome() {
            return outcome;
        }

        public void setOutcome(String outcome) {
            this.outcome = outcome;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public int compareTo(Procedure o) {
            return getDate().compareTo(o.getDate());
        }

    }

    private class Condition implements Comparable<Condition> {
        //        A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical concept that has risen to a level of concern.
        private String code, displayName, description, type, severity, notes, location;
        private Date effectiveDates;


        public Condition() {
        }

        public Condition(String code, String displayName, String description, String type, Date effectiveDates,
                         String severity, String notes, String location) {
            this.code = code;
            this.displayName = displayName;
            this.description = description;
            this.type = type;
            this.effectiveDates = effectiveDates;
            this.severity = severity;
            this.notes = notes;
            this.location = location;
        }

        //Problems, Conditions, and Diagnoses
        public Condition(org.hl7.fhir.r4.model.Condition condition) {
            new Condition(condition.getCode().getCodingFirstRep().getCode(),
                    condition.getCode().getText(),
                    condition.getStageFirstRep().getAssessmentFirstRep().getDisplay(),
                    condition.getCategoryFirstRep().getText(),
                    condition.getOnsetDateTimeType().getValue(),
                    condition.getSeverity().getText(),
                    condition.getNoteFirstRep().getText(),
                    getLocationFromPatient(condition.getSubject())
            );
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Date getEffectiveDates() {
            return effectiveDates;
        }

        public String getFormattedDate() {
            return formatDate(effectiveDates);
        }

        public void setEffectiveDates(Date effectiveDates) {
            this.effectiveDates = effectiveDates;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public int compareTo(Condition o) {
            return getEffectiveDates().compareTo(o.getEffectiveDates());
        }

    }

    private class DiagnosticReport implements Comparable<DiagnosticReport> {
        private String identifier, category, name, result, status, conclusion, presentedForm, location;
        private Date date;

        public DiagnosticReport(String identifier, String category, String name, Date date, String result,
                                String status, String conclusion, String presentedForm, String location) {
            this.identifier = identifier;
            this.category = category;
            this.name = name;
            this.date = date;
            this.result = result;
            this.status = status;
            this.conclusion = conclusion;
            this.presentedForm = presentedForm;
            this.location = location;
        }

        //findings and interpretation of diagnostic tests performed on patients
        public DiagnosticReport(org.hl7.fhir.r4.model.DiagnosticReport diagnosticReport) {
            setIdentifier(diagnosticReport.getIdentifierFirstRep().getValue());
            setCategory(diagnosticReport.getCategoryFirstRep().getText());
            setName(diagnosticReport.getCode().getCodingFirstRep().getDisplay());
            setDate(diagnosticReport.getEffectiveDateTimeType().getValue());
            setResult(diagnosticReport.getResultFirstRep().getDisplay());
            setStatus(diagnosticReport.getStatus().getDisplay());
            setConclusion(diagnosticReport.getConclusion());
            setPresentedForm(diagnosticReport.getPresentedFormFirstRep().getUrl());
            setLocation(getLocationFromPatient(diagnosticReport.getSubject()));
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public String getFormattedDate() {
            return formatDate(date);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getConclusion() {
            return conclusion;
        }

        public void setConclusion(String conclusion) {
            this.conclusion = conclusion;
        }

        public String getPresentedForm() {
            return presentedForm;
        }

        public void setPresentedForm(String presentedForm) {
            this.presentedForm = presentedForm;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        @Override
        public int compareTo(DiagnosticReport o) {
            return getDate().compareTo(o.getDate());
        }

    }


    private class InsuranceCoverage {
        private String payer, type, policyId, policyHolder, coveredParty, relationship, planInformation;

        public InsuranceCoverage() {
        }

        public InsuranceCoverage(String payer, String type, String policyId, String policyHolder, String coveredParty,
                                 String relationship, String planInformation) {
            this.payer = payer;
            this.type = type;
            this.policyId = policyId;
            this.policyHolder = policyHolder;
            this.coveredParty = coveredParty;
            this.relationship = relationship;
            this.planInformation = planInformation;
        }

        public String getPayer() {
            return payer;
        }

        public void setPayer(String payer) {
            this.payer = payer;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPolicyId() {
            return policyId;
        }

        public void setPolicyId(String policyId) {
            this.policyId = policyId;
        }

        public String getPolicyHolder() {
            return policyHolder;
        }

        public void setPolicyHolder(String policyHolder) {
            this.policyHolder = policyHolder;
        }

        public String getCoveredParty() {
            return coveredParty;
        }

        public void setCoveredParty(String coveredParty) {
            this.coveredParty = coveredParty;
        }

        public String getRelationship() {
            return relationship;
        }

        public void setRelationship(String relationship) {
            this.relationship = relationship;
        }

        public String getPlanInformation() {
            return planInformation;
        }

        public void setPlanInformation(String planInformation) {
            this.planInformation = planInformation;
        }
    }

    private String formatDate(Date date) {
        return date != null ? dateFormat.format(date) : "";
    }
}
