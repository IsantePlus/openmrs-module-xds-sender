package org.openmrs.module.xdssender.api.xds;

import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.RegistryResponseType;
import org.dcm4chee.xds2.infoset.util.DocumentRepositoryPortTypeFactory;
import org.dcm4chee.xds2.infoset.ws.repository.DocumentRepositoryPortType;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.handler.AuthenticationHandler;
import org.openmrs.module.xdssender.api.handler.XdsDocumentMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingProvider;

@Component("xdssender.XdsSender")
public class XdsSender {
	
	@Autowired
	private XdsSenderConfig config;
	
	@Autowired
	private AuthenticationHandler authenticationHandler;
	
	@Autowired
	private XdsDocumentMessageHandler xdsDocumentMessageHandler;
	
	public RegistryResponseType sendProvideAndRegister(ProvideAndRegisterDocumentSetRequestType request) {
		
		DocumentRepositoryPortTypeFactory.addHandler((BindingProvider) DocumentRepositoryPortTypeFactory
		        .getDocumentRepositoryPortSoap12(config.getXdsRepositoryEndpoint()), authenticationHandler);
		
		DocumentRepositoryPortType port = DocumentRepositoryPortTypeFactory.getDocumentRepositoryPortSoap12(config
		        .getXdsRepositoryEndpoint());
		
		DocumentRepositoryPortTypeFactory.addHandler((BindingProvider) port, xdsDocumentMessageHandler);
		
		((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
		    config.getXdsRepositoryUsername());
		((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
		    config.getXdsRepositoryPassword());
		return port.documentRepositoryProvideAndRegisterDocumentSetB(request);
	}
}
