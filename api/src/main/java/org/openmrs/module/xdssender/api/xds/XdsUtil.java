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

        Map<String, Object> ccdStringMap = new HashMap<>();
        List<Bundle.BundleEntryComponent> entry = resource.getEntry();
        List<VitalSign> vitalSigns = new ArrayList<>();
        List<CcdEncounter> encounters = new ArrayList<>();
        List<MedicationPrescription> medicationPrescriptions = new ArrayList<>();
        List<Medication> medications = new ArrayList<>();
        List<Immunization> immunizations = new ArrayList<>();
        List<ProcedureRequest> procedureRequests = new ArrayList<>();
        List<Procedure> procedures = new ArrayList<>();
        List<DiagnosticReport> diagnosticReports = new ArrayList<>();
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
//                    TODO - Rework all the mappings from Observations
                    VitalSign vitalSign = mapObservationResource(ccdStringMap, (Observation) eResource);
                    vitalSigns.add(vitalSign);
                    break;
                }
                case "Encounter": {
                    CcdEncounter encounter = mapEncounterResource(ccdStringMap, (org.hl7.fhir.r4.model.Encounter) eResource);
                    encounters.add(encounter);
                    break;
                }
                case "AllergyIntolerance": {
                    AllergyIntolerance allergyIntolerance = mapAllergyIntoleranceResource(ccdStringMap, (org.hl7.fhir.r4.model.AllergyIntolerance) eResource);
                    intolerances.add(allergyIntolerance);
                    break;
                }
                case "MedicationRequest": {
                    MedicationPrescription medicationRequest = mapPrescriptionResource(ccdStringMap, (org.hl7.fhir.r4.model.MedicationRequest) eResource);
                    medicationPrescriptions.add(medicationRequest);
                    break;
                }
                case "Medication": {
                    Medication medication = mapMedicationResource(ccdStringMap, (org.hl7.fhir.r4.model.Medication) eResource);
                    medications.add(medication);
                    break;
                }
                case "Immunization": {
                    Immunization medication = mapImmunizationResource(ccdStringMap, (org.hl7.fhir.r4.model.Immunization) eResource);
                    immunizations.add(medication);
                    break;
                }
                case "ProcedureRequest": {
//					ProcedureRequest procedureRequest = mapProcedureRequestResource(ccdStringMap, (org.hl7.fhir.r4.model.) eResource);
//					procedureRequests.add(procedureRequest);
//                    break;
                }
                case "Procedure": {
                    Procedure procedure = mapProcedureResource(ccdStringMap, (org.hl7.fhir.r4.model.Procedure) eResource);
                    procedures.add(procedure);
                    break;
                }
                case "Condition": {
                    //Problems, Conditions, and Diagnoses
                    Condition condition = mapConditionResource(ccdStringMap, (org.hl7.fhir.r4.model.Condition) eResource);
                    conditions.add(condition);

                    break;
                }
                case "DiagnosticReport": {
                    //findings and interpretation of diagnostic tests performed on patients
                    DiagnosticReport diagnosticReport = mapDiagnosticReportResource(ccdStringMap, (org.hl7.fhir.r4.model.DiagnosticReport) eResource);
                    diagnosticReports.add(diagnosticReport);

                    break;
                }

            }

        }

        ccdStringMap.put("vitalSigns", vitalSigns);
        ccdStringMap.put("encounters", encounters);
        ccdStringMap.put("intolerances", intolerances);
        ccdStringMap.put("medications", medications);
        ccdStringMap.put("medicationPrescriptions", medicationPrescriptions);
        ccdStringMap.put("immunizations", immunizations);
        ccdStringMap.put("procedures", procedures);
        ccdStringMap.put("conditions", conditions);
        ccdStringMap.put("diagnosticReports", diagnosticReports);
        GStringTemplateEngine templateEngine = new GStringTemplateEngine();
        String htmlString = templateEngine.createTemplate(ccdTemplate).make(ccdStringMap).toString();
        return htmlString;
    }

    private Procedure mapProcedureResource(Map<String, Object> ccdStringMap, org.hl7.fhir.r4.model.Procedure procedure) {
        return new Procedure(procedure);
    }

    private DiagnosticReport mapDiagnosticReportResource(Map<String, Object> ccdStringMap, org.hl7.fhir.r4.model.DiagnosticReport diagnosticReport) {
        return new DiagnosticReport(diagnosticReport);
    }

    private Condition mapConditionResource(Map<String, Object> ccdStringMap, org.hl7.fhir.r4.model.Condition condition) {
        return new Condition(condition);
    }

    private ProcedureRequest mapProcedureRequestResource(Map<String, Object> ccdStringMap, org.hl7.fhir.r4.model.Procedure procedure) {
        //    	TODO - Finish the mappings
        return new ProcedureRequest();
    }

    private Immunization mapImmunizationResource(Map<String, Object> ccdStringMap, org.hl7.fhir.r4.model.Immunization immunization) {
        return new Immunization(immunization);
    }

    private Medication mapMedicationResource(Map<String, Object> ccdStringMap, org.hl7.fhir.r4.model.Medication medication) {
        return new Medication(medication);
    }

    private MedicationPrescription mapPrescriptionResource(Map<String, Object> ccdStringMap, MedicationRequest medicationRequest) {
        return new MedicationPrescription(medicationRequest);
    }

    private AllergyIntolerance mapAllergyIntoleranceResource(Map<String, Object> ccdStringMap, org.hl7.fhir.r4.model.AllergyIntolerance intolerance) {
//    	type,description,substance,reaction, status,dataSource
        return new AllergyIntolerance(intolerance.getCategory().get(0).getDisplay(),
                intolerance.getCode().getCodingFirstRep().getDisplay(),
                intolerance.getReactionFirstRep().getSubstance().getCodingFirstRep().getDisplay(),
                intolerance.getReactionFirstRep().getManifestationFirstRep().getCodingFirstRep().getDisplay(),
                intolerance.getClinicalStatus().getText(),
                intolerance.getMeta().getSource()
        );
    }

    private CcdEncounter mapEncounterResource(Map<String, Object> ccdStringMap, Encounter encounter) {
//    	encounter, providers, location, date, indications, dataSource
        return new CcdEncounter(encounter.getClass_().getCode(),
                encounter.getParticipantFirstRep().getIndividual().getDisplay(),
                encounter.getLocationFirstRep().getLocation().getDisplay(),
                encounter.getMeta().getLastUpdated().toString(),
                null,
                encounter.getMeta().getSource());
    }

    private VitalSign mapObservationResource(Map<String, Object> ccdStringMap, Observation obs) {
//		Parse Observation Resource


//		id, name, value, range, interpretationCode, description, date, dataSource
        return new VitalSign(obs.getId()
                , obs.getCode().getCodingFirstRep().getDisplay()
                , obs.getValueQuantity().getValue().toString()
                , obs.getReferenceRangeFirstRep().getText()
                , obs.getCode().getCodingFirstRep().getCode()
                , obs.getCode().getCodingFirstRep().getDisplay()
                , obs.getIssued().toString(), obs.getMeta().getSource());
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
        ccdStringMap.put("familyName", patientName.getFamily());
        ccdStringMap.put("givenName", patientName.getGiven().toString());
        ccdStringMap.put("birthDate", birthDate);
        ccdStringMap.put("gender", gender);
        ccdStringMap.put("address", addressFirstRep.toString());
        ccdStringMap.put("patientId", patientId.getValue());
        ccdStringMap.put("maritalStatus", maritalStatus.getText());
        ccdStringMap.put("telephone", telephone.getValue());
        ccdStringMap.put("patientGeneralPractitioner", patientGeneralPractitioner.toString());
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

    private class VitalSign {
        private String id, name, value, range, interpretationCode, description, date, dataSource;

        public VitalSign(String id, String name, String value, String range, String interpretationCode, String description, String date, String dataSource) {
            this.id = id;
            this.name = name;
            this.value = value;
            this.range = range;
            this.interpretationCode = interpretationCode;
            this.description = description;
            this.date = date;
            this.dataSource = dataSource;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }
    }

    private class CcdEncounter {
        private String encounter, providers, location, date, indications, dataSource;

        public CcdEncounter(String encounter, String providers, String location, String date, String indications, String dataSource) {
            this.encounter = encounter;
            this.providers = providers;
            this.location = location;
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
    	private String identifier,status,intent,category,authoredOn,requester,reasonCode,dosage;

		public MedicationPrescription(MedicationRequest medicationRequest) {
		}

		public MedicationPrescription(String identifier, String status, String intent, String category, String authoredOn, String requester, String reasonCode, String dosage) {
			this.identifier = identifier;
			this.status = status;
			this.intent = intent;
			this.category = category;
			this.authoredOn = authoredOn;
			this.requester = requester;
			this.reasonCode = reasonCode;
			this.dosage = dosage;
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
	}

    private class Medication {
        private String medication, brandName, startDate, productForm, dose, route, adminInstructions, pharmInstructions, status, indications, reaction, description, dataSource;

        public Medication(org.hl7.fhir.r4.model.Medication medication) {
        }

        public Medication(String medication, String brandName, String startDate, String productForm, String dose, String route, String adminInstructions, String pharmInstructions, String status, String indications, String reaction, String description, String dataSource) {
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

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
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
        private String type, description, substance, reaction, status, dataSource;

        public AllergyIntolerance(String type, String description, String substance, String reaction, String status, String dataSource) {
            this.type = type;
            this.description = description;
            this.substance = substance;
            this.reaction = reaction;
            this.status = status;
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
    }

    private class Immunization {
    	private String identifier,status,vaccineCode,occurrenceDate,manufacturer,lotNumber,site,route,doesQuantity,note;

		public Immunization(org.hl7.fhir.r4.model.Immunization immunization) {
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

		public String getManufacturer() {
			return manufacturer;
		}

		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}

		public String getLotNumber() {
			return lotNumber;
		}

		public void setLotNumber(String lotNumber) {
			this.lotNumber = lotNumber;
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

		public String getDoesQuantity() {
			return doesQuantity;
		}

		public void setDoesQuantity(String doesQuantity) {
			this.doesQuantity = doesQuantity;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}
	}

    private class ProcedureRequest {

    }

    private class Procedure {


		public Procedure(org.hl7.fhir.r4.model.Procedure procedure) {

		}
	}

    private class Condition {
		//Problems, Conditions, and Diagnoses
		public Condition(org.hl7.fhir.r4.model.Condition condition) {

		}

    }

    private class DiagnosticReport {
		//findings and interpretation of diagnostic tests performed on patients
		public DiagnosticReport(org.hl7.fhir.r4.model.DiagnosticReport diagnosticReport) {

		}

    }


}
