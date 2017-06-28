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
	
	@Autowired
	@Qualifier("adminService")
	private AdministrationService administrationService;
	
	public String getIdPattern() {
		// TODO - change default
		return getProperty(ID_PATTERN, "%2$s^^^&%1$s&ISO");
	}
	
	public String getProviderRoot() {
		return getProperty(PROVIDER_ROOT, "1.2.3.4.5.7");
	}
	
	public String getUserRoot() {
		return getProperty(USER_ROOT, "1.2.3.4.5.10");
	}
	
	public String getLocationRoot() {
		return getProperty(LOCATION_ROOT, "1.2.3.4.5.8");
	}
	
	public String getShrRoot() {
		return getProperty(SHR_ROOT, "1.2.3.4.5");
	}
	
	public String getPatientRoot() {
		return getProperty(PATIENT_ROOT, "1.2.3.4.9");
	}
	
	private String getProperty(String name, String defaultVal) {
		return administrationService.getGlobalProperty(name, defaultVal);
	}
}
