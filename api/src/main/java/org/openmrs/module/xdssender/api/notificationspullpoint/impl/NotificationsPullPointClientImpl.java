package org.openmrs.module.xdssender.api.notificationspullpoint.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7InQueue;
import org.openmrs.hl7.HL7Service;
import org.openmrs.module.labintegration.api.hl7.messages.util.OruR01Util;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.notificationspullpoint.NotificationsPullPointClient;
import org.openmrs.module.xdssender.notificationspullpoint.GetMessages;
import org.openmrs.module.xdssender.notificationspullpoint.GetMessagesResponse;
import org.openmrs.module.xdssender.notificationspullpoint.NotificationMessageHolderType;
import org.openmrs.module.xdssender.api.service.CcdService;
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
import org.w3c.dom.Element;

import ca.uhn.hl7v2.model.Message;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component("xdssender.NotificationsPullPointClientImpl")
public class NotificationsPullPointClientImpl extends WebServiceGatewaySupport implements NotificationsPullPointClient {

	// TODO: Move this parameter to the Global Properties section and allow for multiple location tags
	private static final String LOCATION_TAG_NAME = "Login Location";

	private static final Logger log = LoggerFactory.getLogger(NotificationsPullPointClientImpl.class);

	public static final String FACILITY_QNAME = "facility";
	private static final BigInteger MAX_MESSAGES_PER_REQUEST = BigInteger.valueOf(100);

	private String lastRequestDate = "";

	@Autowired
	private XdsSenderConfig config;


	@Override
	public boolean getNewMessages() {
		boolean success = false ;
		lastRequestDate = Context.getAdministrationService().getGlobalProperty(XdsSenderConstants.PULL_NOTIFICATIONS_TASK_LAST_SUCCESS_RUN , "");
		LocationTag loginLocationTag = Context.getLocationService().getLocationTagByName(LOCATION_TAG_NAME);
		List<Location> locations = Context.getLocationService().getLocationsByTag(loginLocationTag);
		for (Location location : locations) {
			success =  this.getNewMessages(location);
		}
		return success;
	}

	@Override
	public boolean getNewMessages(Location currentLocation) {
		boolean success = false ;
		GetMessages request = new GetMessages();
		String siteCode = null;

		request.setMaximumNumber(MAX_MESSAGES_PER_REQUEST);

		for (LocationAttribute attribute : currentLocation.getAttributes()) {
			if (attribute.getAttributeType().getUuid().equals(XdsSenderConstants.LOCATION_SITECODE_ATTRIBUTE_UUID)) {
				siteCode = attribute.getValue().toString();
			}
		}

		log.debug("Location SiteCode, Name: ID: SiteCode {}",
		    currentLocation.getName() + ": " + currentLocation.getId() + ": " + siteCode);
		request.getOtherAttributes().put(new QName(FACILITY_QNAME), siteCode);

		GetMessagesResponse response;
		try {
			response = getResponseHttpClient(request);
			HL7Service hl7Service = Context.getHL7Service();
			if (response != null) {
				success = true;
				for (NotificationMessageHolderType notification : response.getNotificationMessage()) {
					Element el = (Element) notification.getMessage().getAny();
					String decodedMessage = new String(Base64.decodeBase64(el.getTextContent().getBytes()));
					// Replace new line character with it's ASCII equivalent
					// Remove the time component from the birthdate to fix a HL7 parsing error
					String parsedMessage = OruR01Util
							.changeMessageVersionFrom251To25(decodedMessage.replace("\n", Character.toString((char) 13))
									.replaceAll("\\[[0-9]{4}\\]", ""));

					log.debug(parsedMessage);
					HL7InQueue hl7InQueue = new HL7InQueue();
					hl7InQueue.setHL7Data(parsedMessage);
					hl7InQueue.setHL7Source(hl7Service.getHL7Source(1));
					hl7InQueue.setHL7SourceKey(currentLocation.getName());
					hl7Service.saveHL7InQueue(hl7InQueue);
				}
			}
		}
		catch (Exception e) {
			success = false;
			log.error("Error getting response in NotificationsPullPointClientImpl: ", e);
		}

		return success;
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

	private GetMessagesResponse getResponseHttpClient(GetMessages requestPayload) throws Exception {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.openmrs.module.xdssender.notificationspullpoint");
		marshaller.afterPropertiesSet();
		StringResult result = new StringResult();
		marshaller.marshal(requestPayload, result);

		OkHttpClient client = new OkHttpClient().newBuilder().build();
		MediaType mediaType = MediaType.parse("text/xml; charset=utf-8");
		String facilitySiteCode = requestPayload.getOtherAttributes().get(new QName(FACILITY_QNAME));
		String sinceAttribute = StringUtils.isNotBlank(lastRequestDate) && isValidISODate(lastRequestDate) ? String.format("since=\"%s\"", lastRequestDate)
				: "";
		String maximumNumberElement = StringUtils.isBlank(lastRequestDate)
				? "<ns2:MaximumNumber>100</ns2:MaximumNumber>\r\n"
				: "";

		String getMessagesPayload = String.format("<SOAP-ENV:Envelope\r\n  "
				+ "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
				+ "  <SOAP-ENV:Header/>\r\n"
				+ "  <SOAP-ENV:Body>\r\n"
				+ "    <ns2:GetMessages\r\n"
				+ "      xmlns:ns2=\"http://docs.oasis-open.org/wsn/b-2\"\r\n"
				+ "      xmlns:ns3=\"http://www.w3.org/2005/08/addressing\"\r\n"
				+ "      xmlns:ns4=\"http://docs.oasis-open.org/wsrf/bf-2\"\r\n"
				+ "      xmlns:ns5=\"http://docs.oasis-open.org/wsn/t-1\"\r\n"
				+ "      xmlns:ns6=\"http://docs.oasis-open.org/wsn/br-2\"\r\n"
				+ "      facility=\"%s\" %s>\r\n"
				+ "      %s"
				+ "    </ns2:GetMessages>\r\n"
				+ "  </SOAP-ENV:Body>\r\n"
				+ "</SOAP-ENV:Envelope>",
		    facilitySiteCode ,sinceAttribute ,maximumNumberElement);
			log.debug(getMessagesPayload);

		RequestBody body = RequestBody.create(getMessagesPayload, mediaType);
		Request request = new Request.Builder().url(config.getNotificationsPullPointEndpoint()).method("POST", body)
		        .addHeader("Content-Type", "text/xml; charset=utf-8")
		        .addHeader("Accept", "text/xml, text/html")
		        .addHeader("Authorization", generateBasicAuthenticationHeader(config.getNotificationsPullPointUsername(),
		            config.getNotificationsPullPointPassword()))
		        .build();

		try (Response response = client.newCall(request).execute()) {
			JAXBContext jaxbContext = JAXBContext.newInstance("org.openmrs.module.xdssender.notificationspullpoint");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			if (response.body() != null) {
				return (GetMessagesResponse) unmarshaller.unmarshal(response.body().byteStream());
			}

			return null;
		}
	}

	private boolean isValidISODate(String dateString) {
		try {
			DateTimeFormatter.ISO_INSTANT.parse(dateString);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
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
			log.debug(conn.getRequestProperty("Content-Length"));
		}
	}

	private static String generateBasicAuthenticationHeader(String userName, String userPassword) {
		byte[] bytesEncoded = Base64.encodeBase64((userName + ":" + userPassword).getBytes(StandardCharsets.UTF_8));
		return "Basic " + new String(bytesEncoded, StandardCharsets.UTF_8);
	}

}
