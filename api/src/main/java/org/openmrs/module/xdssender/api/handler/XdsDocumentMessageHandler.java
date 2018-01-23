package org.openmrs.module.xdssender.api.handler;

import org.dcm4chee.xds2.infoset.util.SentSOAPLogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Collections;
import java.util.Set;

@Component("xdsDocumentMessageHandler")
public class XdsDocumentMessageHandler implements SOAPHandler<SOAPMessageContext> {
	
	private static final Logger log = LoggerFactory.getLogger(SentSOAPLogHandler.class);
	
	@Override
	public Set<QName> getHeaders() {
		return Collections.emptySet();
	}
	
	@Override
	public boolean handleMessage(SOAPMessageContext ctx) {
		boolean isOutboundMessage = (Boolean) ctx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (isOutboundMessage) {
			
			SOAPMessage message = ctx.getMessage();
			SOAPEnvelope envelope;
			try {
				envelope = message.getSOAPPart().getEnvelope();
				SOAPBody body = envelope.getBody();
				removeAttributesFromBodyTag(body);
			}
			catch (SOAPException e) {
				log.debug("Error processing message in XdsDocumentMessageHandler: ", e);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean handleFault(SOAPMessageContext soapMessageContext) {
		return true;
	}
	
	@Override
	public void close(MessageContext messageContext) {
	}
	
	private void removeAttributesFromBodyTag(SOAPBody body) {
		NamedNodeMap attributes = body.getAttributes();
		int count = attributes.getLength();
		for (int i = count - 1; i >= 0; i--) {
			Attr attr = (Attr) attributes.item(0);
			body.removeAttributeNode(attr);
		}
	}
}
