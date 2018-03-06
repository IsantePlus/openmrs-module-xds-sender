package org.openmrs.module.xdssender.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.service.XdsErrorHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XdsSenderProperties {
	
	@Autowired
	private XdsSenderConfig config;
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
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
