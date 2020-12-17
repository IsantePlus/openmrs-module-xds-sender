/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.xdssender;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.api.errorhandling.CcdErrorHandlingService;
import org.openmrs.module.xdssender.api.errorhandling.XdsBErrorHandlingService;
import org.openmrs.module.xdssender.api.scheduler.PullNotificationsTask;
import org.springframework.stereotype.Component;

/**
 * Contains module's config.
 */
@Component("xdssender.XdsSenderConfig")
public class XdsSenderConfig {
	
	public final static String MODULE_PRIVILEGE = "XDS Sender Privilege";
	
	private static final String ID_PATTERN = "xdssender.idPattern";
	
	private static final String PROVIDER_ROOT = "xdssender.providerRoot";
	
	private static final String USER_ROOT = "xdssender.userRoot";
	
	private static final String LOCATION_ROOT = "xdssender.locationRoot";
	
	private static final String SHR_ROOT = "xdssender.shrRoot";
	
	private static final String PATIENT_ROOT = "xdssender.patientRoot";
	
	private static final String ENCOUNTER_ROOT = "xdssender.encounterRoot";
	
	private static final String OBS_ROOT = "xdssender.obsRoot";
	
	private static final String ECID_ROOT = "xdssender.ecidRoot";
	
	private static final String CODE_NATIONAL_ROOT = "xdssender.codeNationalRoot";
	
	private static final String CODE_ST_ROOT = "xdssender.codeStRoot";
	
	private static final String XDS_REPO_ENDPOINT = "xdssender.repositoryEndpoint";
	
	private final static String XDS_REPO_USERNAME = "xdssender.xdsrepository.username";
	
	private final static String XDS_REPO_PASSWORD = "xdssender.xdsrepository.password";
	
	private final static String OPENMRS_USERNAME = "xdssender.openmrs.username";
	
	private final static String OPENMRS_PASSWORD = "xdssender.openmrs.password";
	
	private final static String XDS_ROLE_CLINICIAN = "xdssender.openmrs.provider.role.clinician";
	
	private final static String XDS_ROLE_DOCTOR = "xdssender.openmrs.provider.role.doctor";
	
	private final static String XDS_MODULE_USED_TO_DETERMINE_SOFTWARE_VERSION = "xdssender.openmrs.moduleUsedToDetermineSoftwareVersion";

	private static final String XDSSENDER_EXPORT_CCD_ENDPOINT = "xdssender.exportCcdEndpoint";

	private static final String XDSSENDER_OSHR_USERNAME = "xdssender.oshr.username";

	private static final String XDSSENDER_OSHR_PASSWORD = "xdssender.oshr.password";

	private static final String XDSSENDER_EXPORT_CCD_IGNORE_CERTS = "xdssender.exportCcd.ignoreCerts";

	private final static String CCD_ERROR_HANDLER_IMPLEMENTATION = "xdssender.ccd.errorHandler.implementation";

	private final static String XDS_B_ERROR_HANDLER_IMPLEMENTATION = "xdssender.xdsB.errorHandler.implementation";

	private static final String PULL_NOTIFICATIONS_TASK_INTERVAL = "xdssender.pullNotificationsTaskInterval";

	private static final String NOTIFICATIONS_PULL_POINT_ENDPOINT = "xdssender.notificationsPullPoint.endpoint";

	private static final String NOTIFICATIONS_PULL_POINT_USERNAME = "xdssender.notificationsPullPoint.username";

	private static final String NOTIFICATIONS_PULL_POINT_PASSWORD = "xdssender.notificationsPullPoint.password";

	private static final String SHR_TYPE = "xdssender.shrType";

	private static final String MPI_ENDPOINT = "xdssender.mpiEndpoint";


	public static XdsSenderConfig getInstance() {
		return Context.getRegisteredComponent("xdssender.XdsSenderConfig", XdsSenderConfig.class);
	}
	
	public String getIdPattern() {
		// TODO - change default
		return getProperty(ID_PATTERN, "%2$s^^^&%1$s&ISO");
	}
	
	public String getShrRoot() {
		return getProperty(SHR_ROOT, "1.2.3.4.5");
	}

	public String getMpiEndpoint() {
		return getProperty(MPI_ENDPOINT, "1.2.3.4.5");
	}

	public String getEncounterRoot() {
		return getProperty(ENCOUNTER_ROOT, "1.2.3.4.5.2");
	}
	
	public String getObsRoot() {
		return getProperty(ENCOUNTER_ROOT, "1.2.3.4.5.3");
	}
	
	public String getProviderRoot() {
		return getProperty(PROVIDER_ROOT, "http://ohie-fr:8080/api/users");
	}
	
	public String getLocationRoot() {
		return getProperty(LOCATION_ROOT, "http://ohie-fr:8080/api/organisationUnits");
	}
	
	public String getPatientRoot() {
		return getProperty(PATIENT_ROOT, "1.2.3");
	}
	
	public String getUserRoot() {
		return getProperty(USER_ROOT, "1.2.3.4.5.10");
	}
	
	public String getEcidRoot() {
		return getProperty(ECID_ROOT, "2.16.840.1.113883.4.56");
	}

	public String getShrType() { return getProperty(SHR_TYPE, "fhir"); }

	public String getCodeNationalRoot() {
		return getProperty(CODE_NATIONAL_ROOT, "2.25.212283553061960040061731875660599129565");
	}
	
	public String getCodeStRoot() {
		return getProperty(CODE_ST_ROOT, "2.25.276946543544871160225835991160192746993");
	}
	
	public String getOpenmrsUsername() {
		return getProperty(OPENMRS_USERNAME, "");
	}
	
	public String getOpenmrsPassword() {
		return getProperty(OPENMRS_PASSWORD, "");
	}
	
	public String getXdsRepositoryEndpoint() {
		return getProperty(XDS_REPO_ENDPOINT, "http://sedish.net:5001/xdsrepository");
	}
	
	public String getXdsRepositoryUsername() {
		return getProperty(XDS_REPO_USERNAME, "xds");
	}
	
	public String getXdsRepositoryPassword() {
		return getProperty(XDS_REPO_PASSWORD, "1234");
	}

	public String getProviderRoleClinician() {
		return getProperty(XDS_ROLE_CLINICIAN, "Clinician");
	}
	
	public String getProviderRoleDoctor() {
		return getProperty(XDS_ROLE_DOCTOR, "Doctor");
	}
	
	public String getModuleUsedToDetermineSoftwareVersion() {
		return getProperty(XDS_MODULE_USED_TO_DETERMINE_SOFTWARE_VERSION, "isanteplusreports");
	}
	
	public String getExportCcdEndpoint() {
		return getProperty(XDSSENDER_EXPORT_CCD_ENDPOINT);
	}

	public String getLocalPatientIdRoot() { return getProperty("mpi-client.pid.local"); }
	
	public String getOshrUsername() {
		return getProperty(XDSSENDER_OSHR_USERNAME);
	}
	
	public String getOshrPassword() {
		return getProperty(XDSSENDER_OSHR_PASSWORD);
	}
	
	public Boolean getExportCcdIgnoreCerts() {
		return Boolean.parseBoolean(getProperty(XDSSENDER_EXPORT_CCD_IGNORE_CERTS));
	}

	public CcdErrorHandlingService getCcdErrorHandlingService() {
		String propertyName = CCD_ERROR_HANDLER_IMPLEMENTATION;
		CcdErrorHandlingService handler = null;
		if (isPropertySet(propertyName)) {
			handler = getComponentByGlobalProperty(propertyName, CcdErrorHandlingService.class);
		}
		return handler;
	}

	public XdsBErrorHandlingService getXdsBErrorHandlingService() {
		String propertyName = XDS_B_ERROR_HANDLER_IMPLEMENTATION;
		XdsBErrorHandlingService handler = null;
		if (isPropertySet(propertyName)) {
			handler = getComponentByGlobalProperty(propertyName, XdsBErrorHandlingService.class);
		}
		return handler;
	}

	public Long getPullNotificationsTaskInterval() {
		return Long.parseLong(getProperty(PULL_NOTIFICATIONS_TASK_INTERVAL, PullNotificationsTask.DEFAULT_INTERVAL_SECONDS));
	}

	public String getNotificationsPullPointEndpoint() {
		return getProperty(NOTIFICATIONS_PULL_POINT_ENDPOINT);
	}

	public String getNotificationsPullPointUsername() {
		return getProperty(NOTIFICATIONS_PULL_POINT_USERNAME);
	}

	public String getNotificationsPullPointPassword() {
		return getProperty(NOTIFICATIONS_PULL_POINT_PASSWORD);
	}

	private String getProperty(String name, String defaultVal) {
		return Context.getAdministrationService().getGlobalProperty(name, defaultVal);
	}
	
	private String getProperty(String propertyName) {
		String propertyValue = Context.getAdministrationService().getGlobalProperty(propertyName);
		if (StringUtils.isBlank(propertyValue)) {
			throw new APIException(String.format("Property value for '%s' is not set", propertyName));
		}
		return propertyValue;
	}

	private boolean isPropertySet(String globalProperty) {
		return StringUtils.isNotBlank(Context.getAdministrationService().getGlobalProperty(globalProperty));
	}

	private <T> T getComponentByGlobalProperty(String propertyName, Class<T> type) {
		return Context.getRegisteredComponent(getProperty(propertyName), type);
	}


}
