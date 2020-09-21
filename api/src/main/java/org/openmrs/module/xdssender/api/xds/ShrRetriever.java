package org.openmrs.module.xdssender.api.xds;

import java.util.stream.Collectors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("xdssender.ShrRetriever")
public class ShrRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShrRetriever.class);

	private static final String ECID_NAME = "ECID";

	@Autowired
	private XdsSenderConfig config;

	// Todo: fix autowiring
	// @Autowired
	// @Qualifier("fhirContext")
	private FhirContext fhirContext;

	public void setFhirContext(FhirContext fhirContext) {
		this.fhirContext = fhirContext;
	}

	public Bundle sendRetrieveCCD(Patient patient) {
		try {
			String mpiIdentifier = config.getLocalPatientIdRoot() + patient.getPatientIdentifier().getIdentifier();

			IGenericClient mpiClient = fhirContext.getRestfulClientFactory().newGenericClient(config.getMpiEndpoint());
			// By default, just get this instance's patient
			String patientIds = patient.getUuid();

			// Send request to MPI with the patient info, and get all of the identifiers for linked Patient resources
			Bundle linkedPatientBundle = mpiClient.search()
					.byUrl("/Patient?identifier=urn:ietf:rfc:3986|" + mpiIdentifier + "&_include=Patient:link")
					.returnBundle(Bundle.class).execute();

			// Loop through the Golden Record Patient resource links
			try {
				for (Bundle.BundleEntryComponent entry : linkedPatientBundle.getEntry()) {
					Resource candidatePatient = entry.getResource();

					if (candidatePatient.hasType("Patient") &&
							candidatePatient.getMeta().getTagFirstRep().getCode().equals("5c827da5-4858-4f3d-a50c-62ece001efea")
							&& ((org.hl7.fhir.r4.model.Patient) candidatePatient).hasLink()) {
						// Join ids of all MPI-linked patients
						patientIds = ((org.hl7.fhir.r4.model.Patient) candidatePatient).getLink().stream()
								.map(link -> link.getOther().getResource().getIdElement().getIdPart()).collect(
										Collectors.joining(","));
					}
				}
			} catch (Exception e) {
				LOGGER.error("Failed to get linked Patients from MPI: ", e);
				patientIds = patient.getUuid();
			}

			// Get SHR Bundle for collected patient ids
			IGenericClient shrClient = fhirContext.getRestfulClientFactory().newGenericClient(config.getExportCcdEndpoint());

			return shrClient.search().byUrl("/Patient?_id=" + patientIds + "&_revinclude=*").returnBundle(Bundle.class)
					.execute();
		}
		catch (Exception ex) {
			LOGGER.error("Error when fetching ccd", ex);
			return null;
		}
	}

	private String extractPatientEcid(Patient patient) {
		String patientEcid = null;
		for (PatientIdentifier patientIdentifier : patient.getIdentifiers()) {
			if (patientIdentifier.getIdentifierType().getName().equals(ECID_NAME)) {
				patientEcid = patientIdentifier.getIdentifier();
			}
		}
		return patientEcid;
	}
}
