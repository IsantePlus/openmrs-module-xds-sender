package org.openmrs.module.xdssender;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.api.UserService;
import org.openmrs.module.outgoingmessageexceptions.OutgoingMessage;
import org.openmrs.module.outgoingmessageexceptions.api.dao.OutgoingMessageExceptionsDao;
import org.openmrs.module.outgoingmessageexceptions.api.impl.OutgoingMessageExceptionsServiceImpl;

import java.util.Date;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OutgoingMessageExceptionTest {
	
	@Mock
	OutgoingMessageExceptionsServiceImpl basicModuleService;

	@Mock
	OutgoingMessageExceptionsDao dao;
	
	@Mock
	UserService userService;
	
	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * Get the links for the extension class
	 */
	@Test
	@Ignore
	public void testSavingOutgoingErrorMessage() {
		
		OutgoingMessage outgoingMessage = new OutgoingMessage();
		outgoingMessage.setOwner(userService.getUser(1));
		outgoingMessage.setMessageBody("message");
		outgoingMessage.setType("Hl7");
		outgoingMessage.setDestination("Encounter");
		Date date = new Date(2017, 11, 11);
		outgoingMessage.setTimestamp(date);

		basicModuleService.saveItem(outgoingMessage);
		
		//Then
		OutgoingMessage savedOutgoingMessage = basicModuleService.getItemByUuid(outgoingMessage.getUuid());
		
		assertThat(savedOutgoingMessage, hasProperty("uuid", is(outgoingMessage.getUuid())));
		assertThat(savedOutgoingMessage, hasProperty("owner", is(outgoingMessage.getOwner())));
		
	}
}
