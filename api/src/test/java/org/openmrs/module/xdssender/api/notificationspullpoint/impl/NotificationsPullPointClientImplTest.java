package org.openmrs.module.xdssender.api.notificationspullpoint.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.springframework.ws.client.core.WebServiceTemplate;

@RunWith(MockitoJUnitRunner.class)
public class NotificationsPullPointClientImplTest {
	
	@Mock
	private WebServiceTemplate webServiceTemplate;
	
	@InjectMocks
	private NotificationsPullPointClientImpl notificationsPullPointClientImpl;
	
	@Mock
	private XdsSenderConfig config;
	
	
	@Test
	public void testGetNewMessages() throws Exception {
		
		NotificationsPullPointClientImpl nppc = new NotificationsPullPointClientImpl();
		
		nppc.getNewMessages();
		//throw new RuntimeException("not yet implemented");
	}
	
}
