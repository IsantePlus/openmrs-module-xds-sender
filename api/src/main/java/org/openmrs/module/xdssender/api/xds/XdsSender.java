package org.openmrs.module.xdssender.api.xds;

import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.RegistryResponseType;
import org.dcm4chee.xds2.infoset.util.DocumentRepositoryPortTypeFactory;
import org.dcm4chee.xds2.infoset.ws.repository.DocumentRepositoryPortType;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.handler.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingProvider;

@Component("xdssender.XdsSender")
public class XdsSender {
	
	@Autowired
	private XdsSenderConfig config;
	
	public RegistryResponseType sendProvideAndRegister(ProvideAndRegisterDocumentSetRequestType request) {
		
		DocumentRepositoryPortTypeFactory.addHandler((BindingProvider) DocumentRepositoryPortTypeFactory
		        .getDocumentRepositoryPortSoap12(config.getXdsRepositoryEndpoint()), new AuthenticationHandler());
		
		DocumentRepositoryPortType port = DocumentRepositoryPortTypeFactory.getDocumentRepositoryPortSoap12(config
		        .getXdsRepositoryEndpoint());
		return port.documentRepositoryProvideAndRegisterDocumentSetB(request);
	}
}
