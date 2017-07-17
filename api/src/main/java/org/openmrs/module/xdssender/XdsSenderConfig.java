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

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	
	private static final String XDS_REPO_ENDPOINT = "xdssender.repositoryEndpoint";
	
	private final static String XDS_REPO_USERNAME = "xdssender.xdsrepository.username";
	
	private final static String XDS_REPO_PASSWORD = "xdssender.xdsrepository.password";
	
	// locking object
	private final static Object s_lockObject = new Object();
	
	// Instance
	private static XdsSenderConfig s_instance = null;
	
	public static XdsSenderConfig getInstance() {
		if (s_instance == null)
			synchronized (s_lockObject) {
				if (s_instance == null)
					s_instance = new XdsSenderConfig();
			}
		return s_instance;
	}
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	public String getIdPattern() {
		// TODO - change default
		return getProperty(ID_PATTERN, "%2$s^^^&%1$s&ISO");
	}
	
	public String getShrRoot() {
		return getProperty(SHR_ROOT, "1.2.3.4.5");
	}
	
	public String getEncounterRoot() {
		return getProperty(ENCOUNTER_ROOT, "1.2.3.4.5.2");
	}
	
	public String getObsRoot() {
		return getProperty(ENCOUNTER_ROOT, "1.2.3.4.5.3");
	}
	
	public String getProviderRoot() {
		return getProperty(PROVIDER_ROOT, "1.2.3.4.5.7");
	}
	
	public String getLocationRoot() {
		return getProperty(LOCATION_ROOT, "1.2.3.4.5.8");
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
	
	public String gettXdsUsername() {
		return getProperty(XDS_REPO_USERNAME, "");
	}
	
	public String getXdsPassword() {
		return getProperty(XDS_REPO_PASSWORD, "");
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
	
	private String getProperty(String name, String defaultVal) {
		return Context.getAdministrationService().getGlobalProperty(name, defaultVal);
	}
}
