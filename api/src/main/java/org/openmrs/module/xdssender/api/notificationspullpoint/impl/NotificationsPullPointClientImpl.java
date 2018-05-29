package org.openmrs.module.xdssender.api.notificationspullpoint.impl;

import ca.uhn.hl7v2.model.Message;
import org.apache.commons.codec.binary.Base64;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.notificationspullpoint.NotificationsPullPointClient;
import org.openmrs.module.xdssender.notificationspullpoint.GetMessages;
import org.openmrs.module.xdssender.notificationspullpoint.GetMessagesResponse;
import org.openmrs.module.xdssender.notificationspullpoint.NotificationMessageHolderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Component("xdssender.NotificationsPullPointClientImpl")
public class NotificationsPullPointClientImpl extends WebServiceGatewaySupport implements NotificationsPullPointClient {
	
	private BigInteger MAX_MESSAGES_PER_REQUEST = BigInteger.valueOf(100);
	
	@Autowired
	private XdsSenderConfig config;
	
	@Override
	public List<Message> getNewMessages() {
		GetMessages request = new GetMessages();

		request.setMaximumNumber(MAX_MESSAGES_PER_REQUEST);

		GetMessagesResponse response = (GetMessagesResponse) getResponse(request);

		List<Message> result = new ArrayList<>();

		for (NotificationMessageHolderType notification : response.getNotificationMessage()) {
			//TODO: Ensure that this casting is working properly
			result.add((Message) notification.getMessage());
		}

		return result;
	}
	
	private Object getResponse(Object requestPayload) {
		
		WebServiceMessageCallback addAuthorizationHeader = new WebServiceMessageCallback() {
			
			@Override
			public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
				addAuthorizationHeader();
			}
		};
		
		return getWebServiceTemplate().marshalSendAndReceive(config.getNotificationsPullPointEndpoint(), requestPayload,
		    addAuthorizationHeader);
	}
	
	private void addAuthorizationHeader() {
		TransportContext context = TransportContextHolder.getTransportContext();
		HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
		connection.getConnection().addRequestProperty(
		    "Authorization",
		    generateBasicAuthenticationHeader(config.getNotificationsPullPointUsername(),
		        config.getNotificationsPullPointPassword()));
	}
	
	private static String generateBasicAuthenticationHeader(String userName, String userPassword) {
		byte[] bytesEncoded = Base64.encodeBase64((userName + ":" + userPassword).getBytes(Charset.forName("UTF-8")));
		return "Basic " + new String(bytesEncoded, Charset.forName("UTF-8"));
	}
}
