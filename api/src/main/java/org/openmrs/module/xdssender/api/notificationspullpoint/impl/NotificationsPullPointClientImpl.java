package org.openmrs.module.xdssender.api.notificationspullpoint.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.pool.ConnFactory;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.notificationspullpoint.NotificationsPullPointClient;
import org.openmrs.module.xdssender.notificationspullpoint.GetMessages;
import org.openmrs.module.xdssender.notificationspullpoint.GetMessagesResponse;
import org.openmrs.module.xdssender.notificationspullpoint.NotificationMessageHolderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;
import org.springframework.xml.transform.StringResult;

import ca.uhn.hl7v2.model.Message;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component("xdssender.NotificationsPullPointClientImpl")
public class NotificationsPullPointClientImpl extends WebServiceGatewaySupport implements NotificationsPullPointClient {

	private static final Logger log = LoggerFactory.getLogger(NotificationsPullPointClientImpl.class);

	private BigInteger MAX_MESSAGES_PER_REQUEST = BigInteger.valueOf(100);

	@Autowired
	private XdsSenderConfig config;

	@Override
	public List<Message> getNewMessages() {
		GetMessages request = new GetMessages();

		request.setMaximumNumber(MAX_MESSAGES_PER_REQUEST);

		GetMessagesResponse response;
		try {
			// response = (GetMessagesResponse) getResponse(request);
			response = (GetMessagesResponse) getResponseHttpClient(request);
			List<Message> result = new ArrayList<>();

			for (NotificationMessageHolderType notification : response.getNotificationMessage()) {
				// TODO: Ensure that this casting is working properly
				result.add((Message) notification.getMessage().getAny());
			}

			return result;

		} catch (Exception e) {
			log.debug("Error getting response in NotificationsPullPointClientImpl: ", e);
			e.printStackTrace();
		}

		return null;

	}

	private Object getResponse(Object requestPayload) throws Exception {

		WebServiceMessageCallback addAuthorizationHeader = new WebServiceMessageCallback() {

			@Override
			public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
				addAuthorizationHeader(requestPayload);
			}
		};

		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

		marshaller.setContextPath("org.openmrs.module.xdssender.notificationspullpoint");
		marshaller.afterPropertiesSet();

		WebServiceTemplate webServiceTemplate = getWebServiceTemplate();
		webServiceTemplate.setMarshaller(marshaller);
		return webServiceTemplate.marshalSendAndReceive(config.getNotificationsPullPointEndpoint(), requestPayload,
				addAuthorizationHeader);
	}

	private Object getResponseHttpClient(GetMessages requestPayload) throws Exception {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.openmrs.module.xdssender.notificationspullpoint");
		marshaller.afterPropertiesSet();
		StringResult result = new StringResult();
		marshaller.marshal(requestPayload, result);

		OkHttpClient client = new OkHttpClient().newBuilder()
		.build();
		MediaType mediaType = MediaType.parse("text/xml; charset=utf-8");
		RequestBody body = RequestBody.create("<SOAP-ENV:Envelope\r\n  xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n  <SOAP-ENV:Header/>\r\n  <SOAP-ENV:Body>\r\n    <ns2:GetMessages\r\n      xmlns:ns2=\"http://docs.oasis-open.org/wsn/b-2\"\r\n      xmlns:ns3=\"http://www.w3.org/2005/08/addressing\"\r\n      xmlns:ns4=\"http://docs.oasis-open.org/wsrf/bf-2\"\r\n      xmlns:ns5=\"http://docs.oasis-open.org/wsn/t-1\"\r\n      xmlns:ns6=\"http://docs.oasis-open.org/wsn/br-2\">\r\n      <ns2:MaximumNumber>100</ns2:MaximumNumber>\r\n    </ns2:GetMessages>\r\n  </SOAP-ENV:Body>\r\n</SOAP-ENV:Envelope>", mediaType);
		// RequestBody body = RequestBody.create(result.toString(), mediaType);
		Request request = new Request.Builder()
			.url(config.getNotificationsPullPointEndpoint())
			.method("POST", body)
			.addHeader("Content-Type", "text/xml; charset=utf-8")
			.addHeader("Accept", "text/xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
			.addHeader("Accept-Encoding", "gzip")
			.addHeader("Authorization",  generateBasicAuthenticationHeader(
				config.getNotificationsPullPointUsername(), config.getNotificationsPullPointPassword()))
			.build();
		Response response = client.newCall(request).execute();

        JAXBContext jaxbContext = JAXBContext.newInstance("org.oasis_open.docs.wsn.b_2");
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object res = unmarshaller.unmarshal(IOUtils.toInputStream(response.body().string()));

		return res;

	}
	
	private void addAuthorizationHeader() {
		TransportContext context = TransportContextHolder.getTransportContext();
		HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
		connection.getConnection().addRequestProperty("Authorization", generateBasicAuthenticationHeader(
		    config.getNotificationsPullPointUsername(), config.getNotificationsPullPointPassword()));
	}

	private void addAuthorizationHeader(Object requestPayload) {
		log.debug("Setting authorization headers");
		TransportContext context = TransportContextHolder.getTransportContext();
		HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
		HttpURLConnection conn = connection.getConnection();
		conn.addRequestProperty("Authorization", generateBasicAuthenticationHeader(
		    config.getNotificationsPullPointUsername(), config.getNotificationsPullPointPassword()));
		
		if (requestPayload instanceof GetMessages) {
			log.debug("Setting content length");
			// conn.setRequestProperty("Content-Length", "" + 536);
			// conn.setFixedLengthStreamingMode(536);
			log.debug(conn.getRequestProperty("Content-Length"));
		}
	}

	private static String generateBasicAuthenticationHeader(String userName, String userPassword) {
		byte[] bytesEncoded = Base64.encodeBase64((userName + ":" + userPassword).getBytes(Charset.forName("UTF-8")));
		return "Basic " + new String(bytesEncoded, Charset.forName("UTF-8"));
	}
}
