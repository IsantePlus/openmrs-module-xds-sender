package org.openmrs.module.xdssender.api.xds;

import groovy.text.GStringTemplateEngine;
import org.dcm4chee.xds2.infoset.rim.ClassificationType;
import org.dcm4chee.xds2.infoset.rim.ExternalIdentifierType;
import org.dcm4chee.xds2.infoset.rim.InternationalStringType;
import org.dcm4chee.xds2.infoset.rim.LocalizedStringType;
import org.dcm4chee.xds2.infoset.rim.RegistryObjectType;
import org.dcm4chee.xds2.infoset.util.InfosetUtil;
import org.hl7.fhir.r4.model.*;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Component("xdssender.XdsUtil")
public final class XdsUtil {

    @Autowired
    private XdsSenderConfig config;

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
//                    System.out.println("Processing Obs:====> "+obs.getCode().getCodingFirstRep().getDisplay());

                    if (codes.contains(obs.getCode().getCodingFirstRep().getCode())) {
                        vitalSigns.add(mapObservationResource(obs));
                    } else {
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
                                medications.add(medication);
                                break;
                            }
                            case "1271AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA": {
                                DiagnosticReport diagnosticReport = mapDiagnosticReportResource(obs);
                                diagnosticReports.add(diagnosticReport);
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

        Collections.sort(vitalSigns);
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
        String htmlString = templateEngine.createTemplate(ccdTemplate).make(ccdStringMap).toString();
        return htmlString;
    }


    private MedicationPrescription mapMedicationRequest(Observation obs) {
        // String identifier, String status, String intent, String category, String authoredOn, String requester, String reasonCode, String dosage
        return new MedicationPrescription(obs.getValue().toString(),
                obs.getStatus().getDisplay(),
                "",
                obs.hasCode() ? obs.getCode().getCodingFirstRep().getDisplay() : "",
                obs.getEffectiveDateTimeType().getValue().toString(),
                ((Encounter) obs.getEncounter().getResource()).getParticipantFirstRep().getIndividual().getDisplay(),
                "",
                obs.getValue().toString(),
                ((Encounter) obs.getEncounter().getResource()).getLocationFirstRep().getLocation().getDisplay());
     }

    private String getMedName(Observation obs) {
        try {
            return obs.hasHasMember() && obs.getHasMember().size() > 1 ?
                    ((Observation) ((Reference)obs.getHasMember().toArray()[1]).getResource()).getValueCodeableConcept().getCodingFirstRep().getDisplay() :
                    obs.getValue().toString();
        } catch (Exception e) {
            return obs.getValue().toString();
        }
    }

    private Medication mapMedicationList(Observation obs) {
        //        medication, brandName, startDate, productForm, dose, route, adminInstructions, pharmInstructions,
        //                status, indications, reaction, description, dataSource
        return new Medication(
                obs.getValue().toString(),
                getMedName(obs),
                obs.getEffectiveDateTimeType().getValue(),
                "", "", "", "", "",
                obs.getStatus().getDisplay(), "", "",
                obs.getValue().toString(),
                ((Encounter) obs.getEncounter().getResource()).getLocationFirstRep().getLocation().getDisplay());
    }

    private Immunization mapImmunizationResource(Observation obs) {
//        identifier, vaccineCode,doseQuantity, occurrenceDate,site,route,status,
        String identifier = null;
        identifier = getLoincCode(obs);

        return new Immunization(
                obs.getValueCodeableConcept().getCodingFirstRep().getDisplay()
                , obs.getCode().getCodingFirstRep().getCode()
                , ""
                , obs.getEffectiveDateTimeType().getValue().toString(),
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
        String identifier = null;
        identifier = getLoincCode(obs);
        return new DiagnosticReport(
                identifier != null ? identifier : getLoincCode(obs),
                obs.getCode().getCodingFirstRep().getDisplay(),
                obs.getValueCodeableConcept().getCodingFirstRep().getDisplay(),
                obs.getEffectiveDateTimeType().getValue().toString(),
                "", "", "", "");
    }

    private Condition mapConditionResource(org.hl7.fhir.r4.model.Condition condition) {
        return new Condition(condition);
    }

    private Condition mapConditionResource(Observation obs) {
//        code, displayName, description, type, effectiveDates, severity, notes
        String identifier = null;
        identifier = getLoincCode(obs);
        Coding codingFirstRep = obs.getCode().getCodingFirstRep();
        return new Condition(identifier != null ? identifier : codingFirstRep.getCode(),
                obs.getValueCodeableConcept().getCodingFirstRep().getDisplay(),
                codingFirstRep.getDisplay(),
                codingFirstRep.getDisplay(), obs.getIssued(), "", "");
    }

    private String getLoincCode(Observation obs) {
        for (Coding coding : obs.getCode().getCoding()) {
            if ("http://loinc.org".equals(coding.getSystem())) {
                return coding.getCode();
            }
        }

        return obs.getCode().getCodingFirstRep().getCode();
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
//    	type,description,substance,reaction, status,severity,dataSource
        return new AllergyIntolerance(intolerance.getType().getDisplay() + (intolerance.hasCategory() ? " - " + intolerance.getCategory().get(0).getCode() : ""),
                intolerance.getCode().getCodingFirstRep().getDisplay(),
                reactionFirstRep.getSubstance() != null ? reactionFirstRep.getSubstance().getCodingFirstRep().getDisplay() : "",
                reactionFirstRep.getManifestationFirstRep() != null ? reactionFirstRep.getManifestationFirstRep().getCodingFirstRep().getDisplay() : "",
                intolerance.getClinicalStatus() != null ? intolerance.getClinicalStatus().getText() : "",
                reactionFirstRep.getSeverity() != null ? reactionFirstRep.getSeverity().getDisplay() : "",
                intolerance.getMeta().getSource()
        );
    }

    private CcdEncounter mapEncounterResource(Map<String, Object> ccdStringMap, Encounter encounter) {
//    	encounter, providers, location, date, indications, dataSource
        return new CcdEncounter(encounter.getTypeFirstRep().getCodingFirstRep().getDisplay(),
                encounter.getParticipantFirstRep().getIndividual().getDisplay(),
                encounter.getLocationFirstRep().getLocation().getDisplay(),
                encounter.getMeta().getLastUpdated().toString(),
                null,
                encounter.getMeta().getSource(),
                encounter.getTypeFirstRep().getCodingFirstRep().getDisplay());
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
        HumanName patientName = pat.getNameFirstRep();
        Date birthDate = pat.getBirthDate();
        String gender = pat.getGender().getDisplay();
        Address addressFirstRep = pat.getAddressFirstRep();
        org.hl7.fhir.r4.model.Patient.ContactComponent patientContacts = pat.getContactFirstRep();
        Reference patientGeneralPractitioner = pat.getGeneralPractitionerFirstRep();
        Identifier patientId = pat.getIdentifierFirstRep();
        CodeableConcept maritalStatus = pat.getMaritalStatus();
        ContactPoint telephone = pat.getTelecomFirstRep();
        putValue(ccdStringMap,"familyName", patientName.getFamily());
        putValue(ccdStringMap,"givenName", patientName.getGiven().toString().replace("[", "").replace("]", ""));
        putValue(ccdStringMap,"birthDate", birthDate.toString());
        putValue(ccdStringMap,"gender", gender);
        putValue(ccdStringMap,"address", addressFirstRep);





        putValue(ccdStringMap,"patientId", patientId.getValue());
        putValue(ccdStringMap,"maritalStatus", maritalStatus.getText());
        putValue(ccdStringMap,"telephone", telephone.getValue());
        putValue(ccdStringMap,"patientGeneralPractitioner", patientGeneralPractitioner.getDisplay());
//        Not currently returned
        putValue(ccdStringMap,"race", "");
        putValue(ccdStringMap,"language", "");
        putValue(ccdStringMap,"ethnicity", "");
        putValue(ccdStringMap,"guardian", "");

    }

    private static void putValue(Map<String, Object> ccdStringMap, String key, Object value) {
        try {
            if(value != null &&
                    (!ccdStringMap.containsKey(key)
                            || ccdStringMap.get(key) == null
                            || ((String) ccdStringMap.get(key)).isEmpty())) {
                ccdStringMap.put(key, value);
            }
        } catch (Exception e) {
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
            return getDate().compareTo(o.getDate());
        }
    }

    private class CcdEncounter {
        private String encounter, providers, location, date, indications, dataSource, type;

        public CcdEncounter(String encounter, String providers, String location, String date, String indications, String dataSource, String type) {
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
            return providers;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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
    }

    private class MedicationPrescription {
        private String identifier, status, intent, category, authoredOn, requester, reasonCode, dosage, location;

        public MedicationPrescription(MedicationRequest medicationRequest) {
        }

        public MedicationPrescription(String identifier, String status, String intent, String category, String authoredOn, String requester, String reasonCode, String dosage, String location) {
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

        public String getAuthoredOn() {
            return authoredOn;
        }

        public void setAuthoredOn(String authoredOn) {
            this.authoredOn = authoredOn;
        }

        public String getRequester() {
            return requester;
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
        public String getLocation() {  return location;    }

        public void setLocation(String location) {
            this.location = location;
        }

    }

    private class Medication {
        private String medication, brandName, productForm, dose, route, adminInstructions, pharmInstructions,
                status, indications, reaction, description, dataSource;
        private Date startDate;

        public Medication(org.hl7.fhir.r4.model.Medication medication) {
        }

        public Medication(String medication, String brandName, Date startDate, String productForm, String dose, String route, String adminInstructions, String pharmInstructions, String status, String indications, String reaction, String description, String dataSource) {
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
        }

        public String getMedication() {
            return medication;
        }

        public void setMedication(String medication) {
            this.medication = medication;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public Date getStartDate() {
            return startDate;
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
    }

    private class AllergyIntolerance {
        private String type, description, substance, reaction, status, criticality, dataSource;

        public AllergyIntolerance(String type, String description, String substance, String reaction, String status, String criticality, String dataSource) {
            this.type = type;
            this.description = description;
            this.substance = substance;
            this.reaction = reaction;
            this.status = status;
            this.criticality = criticality;
            this.dataSource = dataSource;
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
    }

    private class Immunization {
        private String identifier, vaccineCode, doseQuantity, occurrenceDate, site, route, status, notes;

        public Immunization(String identifier, String vaccineCode, String doseQuantity, String occurrenceDate,
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
            setOccurrenceDate(immunization.getOccurrenceDateTimeType().getValueAsString());
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

        public String getOccurrenceDate() {
            return occurrenceDate;
        }

        public void setOccurrenceDate(String occurrenceDate) {
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

    }

    private class ProcedureRequest {

    }

    private class Procedure {
        private String code, procedure, description, date, indications, outcome;

        public Procedure(String code, String procedure, String description, String date, String indications, String outcome) {
            this.code = code;
            this.procedure = procedure;
            this.description = description;
            this.date = date;
            this.indications = indications;
            this.outcome = outcome;
        }

        public Procedure(org.hl7.fhir.r4.model.Procedure procedure) {
            setCode(procedure.getCode().getCodingFirstRep().getCode());
            setProcedure(procedure.getFocalDeviceFirstRep().getAction().getCodingFirstRep().getDisplay());
            setDescription(procedure.getFocalDeviceFirstRep().getManipulated().getDisplay());
            setDate(procedure.getPerformedDateTimeType().getValueAsString());
            setIndications(procedure.getCategory().getText());
            setOutcome(procedure.getOutcome().getText());

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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
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
    }

    private class Condition {
        //        A clinical condition, problem, diagnosis, or other event, situation, issue, or clinical concept that has risen to a level of concern.
        private String code, displayName, description, type, severity, notes;
        private Date effectiveDates;


        public Condition() {
        }

        public Condition(String code, String displayName, String description, String type, Date effectiveDates,
                         String severity, String notes) {
            this.code = code;
            this.displayName = displayName;
            this.description = description;
            this.type = type;
            this.effectiveDates = effectiveDates;
            this.severity = severity;
            this.notes = notes;
        }

        //Problems, Conditions, and Diagnoses
        public Condition(org.hl7.fhir.r4.model.Condition condition) {
            new Condition(condition.getCode().getCodingFirstRep().getCode(),
                    condition.getCode().getText(),
                    condition.getStageFirstRep().getAssessmentFirstRep().getDisplay(),
                    condition.getCategoryFirstRep().getText(),
                    condition.getOnsetDateTimeType().getValue(),
                    condition.getSeverity().getText(),
                    condition.getNoteFirstRep().getText()
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
    }

    private class DiagnosticReport {
        private String identifier, category, name, date, result, status, conclusion, presentedForm;

        public DiagnosticReport(String identifier, String category, String name, String date, String result,
                                String status, String conclusion, String presentedForm) {
            this.identifier = identifier;
            this.category = category;
            this.name = name;
            this.date = date;
            this.result = result;
            this.status = status;
            this.conclusion = conclusion;
            this.presentedForm = presentedForm;
        }

        //findings and interpretation of diagnostic tests performed on patients
        public DiagnosticReport(org.hl7.fhir.r4.model.DiagnosticReport diagnosticReport) {
            setIdentifier(diagnosticReport.getIdentifierFirstRep().getValue());
            setCategory(diagnosticReport.getCategoryFirstRep().getText());
            setName(diagnosticReport.getCode().getCodingFirstRep().getDisplay());
            setDate(diagnosticReport.getEffectiveDateTimeType().getValueAsString());
            setResult(diagnosticReport.getResultFirstRep().getDisplay());
            setStatus(diagnosticReport.getStatus().getDisplay());
            setConclusion(diagnosticReport.getConclusion());
            setPresentedForm(diagnosticReport.getPresentedFormFirstRep().getUrl());

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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
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
}
