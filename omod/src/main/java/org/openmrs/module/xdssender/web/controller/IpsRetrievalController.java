/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.xdssender.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.xdssender.api.service.CcdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Continuity-of-care retrieval. Pulls a patient's consolidated IPS from the SHR IPS mediator
 * (OpenCR resolves the patient to the golden record; OpenHIM aggregates the SHR clinical data) and
 * returns it rendered for viewing. Read-only: it does not write discrete clinical data into the
 * local record.
 *
 * <p>GET {@code module/xdssender/retrieveIps.form?patientId=<patientUuid>}
 */
@Controller("xdssender.IpsRetrievalController")
@RequestMapping(value = "module/xdssender/retrieveIps.form")
public class IpsRetrievalController {

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private PatientService patientService;

	@Autowired
	private CcdService ccdService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String retrieveIps(@RequestParam("patientId") String patientUuid) {
		Patient patient = patientService.getPatientByUuid(patientUuid);
		if (patient == null) {
			log.warn("IPS retrieval requested for unknown patient uuid: " + patientUuid);
			return "Patient not found: " + patientUuid;
		}

		ccdService.downloadAndSaveIps(patient);
		return ccdService.getHtmlParsedLocallyStoredCcd(patient);
	}
}
