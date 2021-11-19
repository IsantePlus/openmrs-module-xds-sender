package org.openmrs.module.xdssender.api.patient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;

@Component
@Transactional
public class PatientEcidUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientEcidUpdater.class);

    private static final String ECID_UUID = "f54ed6b9-f5b9-4fd5-a588-8f7561a78401";
    private static final String CODE_NATIONAL_UUID = "9fb4533d-4fd5-4276-875b-2ab41597f5dd";
    private static final String FETCHER_BEAN_ID = "registrationcore.mpiPatientFetcherFhir";
    private static final String FETCH_PATIENT_METHOD = "fetchMpiPatient";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PatientService patientService;

    @Autowired
    private LocationService locationService;

    public void fetchEcidIfRequired(Patient patient) {
        if (StringUtils.isBlank(getEcid(patient))) {
            LOGGER.info("Fetching ECID for patient {}", patient.getPatientId());
            updateEcid(patient);
        } else {
            LOGGER.debug("Patient {} already has an ECID", patient.getPatientId());
        }
    }

    private void updateEcid(Patient patient) {
        String codeNational = getCodeNational(patient);
        Patient mpiPatient = fetchMpiPatient(codeNational);

        if (mpiPatient != null) {
            String ecid = getEcid(mpiPatient);

            ecid = ecid == null ? patient.getUuid() : ecid;

            PatientIdentifierType ecidIdType = patientService
                    .getPatientIdentifierTypeByUuid(ECID_UUID);

            patient.addIdentifier(new PatientIdentifier(ecid, ecidIdType,
                    locationService.getDefaultLocation()));

            patientService.savePatient(patient);
        }
    }

    private String getCodeNational(Patient patient) {
        return getIdentifier(patient, CODE_NATIONAL_UUID);
    }

    private String getEcid(Patient patient) {
        return getIdentifier(patient, ECID_UUID);
    }

    private String getIdentifier(Patient patient, String uuid) {
        for (PatientIdentifier identifier : patient.getIdentifiers()) {
            if (uuid.equals(identifier.getIdentifierType().getUuid())) {
                return identifier.getIdentifier();
            }
        }
        return null;
    }

    private Patient fetchMpiPatient(String codeNational) {
        // Using reflection as a quick hack to get around module dependencies
        // No time to do anything else currently

        Object fetcher = applicationContext.getBean(FETCHER_BEAN_ID);

        try {
            return (Patient) MethodUtils.invokeMethod(fetcher, FETCH_PATIENT_METHOD,
                    codeNational, CODE_NATIONAL_UUID);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to invoke PDQ fetcher", e);
        }
    }
}
