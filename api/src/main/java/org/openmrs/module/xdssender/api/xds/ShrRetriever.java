package org.openmrs.module.xdssender.api.xds;

import java.util.ArrayList;
import java.util.stream.Collectors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
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
			String mpiIdentifier = config.getLocalPatientIdRoot() + patient.getUuid();

			IGenericClient mpiClient = getAuthenticatedClient(config.getMpiEndpoint(), config.getOshrUsername(), config.getOshrPassword());

			// By default, just get this instance's patient
			String patientIds = patient.getUuid();

			// Send request to MPI with the patient info, and get all of the identifiers for linked Patient resources
			Bundle linkedPatientBundle = mpiClient.search()
					.byUrl("/Patient?identifier=urn:ietf:rfc:3986|" + mpiIdentifier + "&_include=Patient:link")
					.returnBundle(Bundle.class).execute();

			// Loop through the Golden Record Patient resource links
			try {
				for (Bundle.BundleEntryComponent entry : linkedPatientBundle.getEntry()) {
					org.hl7.fhir.r4.model.Patient candidatePatient;

					if(entry.hasResource() && entry.getResource().hasType("Patient"))
						candidatePatient = (org.hl7.fhir.r4.model.Patient) entry.getResource();
					else {
						LOGGER.info("Can't find linked patients in MPI: non-Patient entry!");
						break;
					}


					if (candidatePatient.hasMeta()
							&& candidatePatient.getMeta().hasTag()
							&& candidatePatient.getMeta().getTagFirstRep().hasCode()
							&& candidatePatient.getMeta().getTagFirstRep().getCode().equals("5c827da5-4858-4f3d-a50c-62ece001efea")
							&& candidatePatient.hasLink()) {

						ArrayList<String> patientIdList = new ArrayList<String>();

						for(org.hl7.fhir.r4.model.Patient.PatientLinkComponent link : candidatePatient.getLink()) {
							if(link.hasOther() && link.getOther().hasReferenceElement() && link.getOther().getReferenceElement().hasIdPart()) {
								// Get linked original uuid
								patientIdList.add(link.getOther().getReferenceElement().getIdPart());
							}

						}

						// Join ids of all MPI-linked patients
						if(!patientIdList.isEmpty()) {
							String mpiPatientIds = String.join(",", patientIdList);

							Bundle linkedPatientIdentifiers = mpiClient.search()
									.byUrl("/Patient?_id=" + mpiPatientIds + "&_elements=identifier")
									.returnBundle(Bundle.class).execute();

							// Get iSantePlus uuids
							if(linkedPatientIdentifiers.hasTotal() && linkedPatientIdentifiers.getTotal() > 0) {
								patientIdList = new ArrayList<>();

								for (Bundle.BundleEntryComponent patientIdentifier : linkedPatientIdentifiers.getEntry()) {
									org.hl7.fhir.r4.model.Patient p = (org.hl7.fhir.r4.model.Patient) patientIdentifier.getResource();
									for(Identifier i : p.getIdentifier()) {
										// TODO remove hardcoded system all over
										if(i.hasSystem() && i.getSystem().equals("urn:ietf:rfc:3986")) {
											String[] uuidString = i.getValue().split("/");
											patientIdList.add(uuidString[uuidString.length - 1]);
											break;
										}
									}
								}
							}

							if(!patientIdList.isEmpty()) {
								patientIds = String.join(",", patientIdList);
							} else {
								LOGGER.info("Can't get iSantePlus uuids from linked patients!");
							}
						} else {
							LOGGER.info("Can't find linked patients in MPI: id list is empty!");
						}

					} else {
						LOGGER.info("Can't find linked patients in current patient resource: badly formed linked patients resource!");
					}
				}
			} catch (Exception e) {
				LOGGER.error("Failed to get linked Patients from MPI: ", e);
				patientIds = patient.getUuid();
			}

			// Get SHR Bundle for collected patient ids
			IGenericClient shrClient = getAuthenticatedClient(config.getExportCcdEndpoint(), config.getOshrUsername(), config.getOshrPassword());
			Bundle returnBundle =  shrClient.search().byUrl("/Patient?_id=" + patientIds + "&_revinclude=*").returnBundle(Bundle.class)
					.execute();

			if(!returnBundle.hasTotal() || returnBundle.getTotal() == 0)
				returnBundle = shrClient.search().byUrl("/Patient?_id=" + patient.getUuid() + "&_revinclude=*").returnBundle(Bundle.class)
						.execute();

			return returnBundle;
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

	public IGenericClient getAuthenticatedClient(String targetUrl, String username, String password) {
		IClientInterceptor authInterceptor = new BasicAuthInterceptor(username, password);
		fhirContext.getRestfulClientFactory().setSocketTimeout(200 * 1000);

		IGenericClient client = fhirContext.getRestfulClientFactory().newGenericClient(targetUrl);
		client.registerInterceptor(authInterceptor);

		return client;
	}
}
