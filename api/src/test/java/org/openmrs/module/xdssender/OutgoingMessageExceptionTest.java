package org.openmrs.module.xdssender;

import org.junit.Test;
import org.openmrs.api.UserService;
import org.openmrs.module.outgoingmessageexceptions.OutgoingMessage;
import org.openmrs.module.outgoingmessageexceptions.api.OutgoingMessageExceptionsService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OutgoingMessageExceptionTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	OutgoingMessageExceptionsService basicModuleService;
	
	@Autowired
	UserService userService;
	
	/**
	 * Get the links for the extension class
	 */
	@Test
	public void testSavingOutgoingErrorMessage() {
		
		OutgoingMessage outgoingMessage = new OutgoingMessage();
		outgoingMessage.setOwner(userService.getUser(1));
		outgoingMessage.setMessageBody("message");
		outgoingMessage.setType("Hl7");
		outgoingMessage.setDestination("Encounter");
		Date date = new Date();
		outgoingMessage.setTimestamp(date);
		
		basicModuleService.saveItem(outgoingMessage);
		
		//Then
		OutgoingMessage savedOutgoingMessage = basicModuleService.getItemByUuid(outgoingMessage.getUuid());
		
		assertThat(savedOutgoingMessage, hasProperty("uuid", is(outgoingMessage.getUuid())));
		assertThat(savedOutgoingMessage, hasProperty("owner", is(outgoingMessage.getOwner())));
		
	}
}
