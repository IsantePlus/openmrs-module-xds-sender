package org.openmrs.module.xdssender.api.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;

public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {
	
	private final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	
	private String username;
	
	private String password;
	
	@Override
	public Set<QName> getHeaders() {
		return null;
	}
	
	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		final Boolean outInd = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		if (outInd.booleanValue()) {
			try {
				context.put(BindingProvider.USERNAME_PROPERTY, username);
				context.put(BindingProvider.PASSWORD_PROPERTY, password);
			}
			catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean handleFault(SOAPMessageContext context) {
		logger.error("Error occurred during authentication");
		return false;
	}
	
	@Override
	public void close(MessageContext context) {
		logger.debug("Closing authentication handler");
	}
}
