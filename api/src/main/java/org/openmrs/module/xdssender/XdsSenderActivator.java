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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.event.Event;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.xdssender.api.cda.EncounterEventListener;
import org.openmrs.module.xdssender.api.scheduler.impl.XdsSenderSchedulerServiceImpl;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class XdsSenderActivator extends BaseModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private EncounterEventListener getEncounterEventListener() {
		return Context.getRegisteredComponent("xdssender.EncounterEventListener", EncounterEventListener.class);
	}
	
	/**
	 * @see #started()
	 */
	@Override
	public void started() {
		log.info("Started Xds Sender");
		Event.subscribe(Encounter.class, null, getEncounterEventListener());
		Context.getRegisteredComponents(XdsSenderSchedulerServiceImpl.class).get(0).runXdsSenderScheduler();
	}
	
	/**
	 * @see #stopped()
	 */
	@Override
	public void stopped() {
		log.info("Shutdown Xds Sender");
		Event.unsubscribe(Encounter.class, null, getEncounterEventListener());
	}
	
}
