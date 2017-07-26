package org.openmrs.module.xdssender.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.service.XdsErrorHandlingService;
import org.springframework.beans.factory.annotation.Autowired;

public class XdsSenderProperties {
	
	@Autowired
	private XdsSenderConfig config = XdsSenderConfig.getInstance();
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	// locking object
	private final static Object s_lockObject = new Object();
	
	// Instance
	private static XdsSenderProperties s_instance = null;
	
	public static XdsSenderProperties getInstance() {
		if (s_instance == null)
			synchronized (s_lockObject) {
				if (s_instance == null)
					s_instance = new XdsSenderProperties();
			}
		return s_instance;
	}
	
	public XdsErrorHandlingService getMpiErrorHandlingService() {
		String handlerId = config.getErrorHandlerImplementation();
		XdsErrorHandlingService handler = null;
		if (StringUtils.isNotBlank(handlerId)) {
			log.debug("Looking up biometrics engine component: " + handlerId);
			handler = Context.getRegisteredComponent(handlerId, XdsErrorHandlingService.class);
			if (handler == null) {
				throw new IllegalStateException(handlerId + " is not valid.");
			}
		}
		return handler;
	}
	
}
