package org.openmrs.module.xdssender.api.xds;

import org.dcm4chee.xds2.infoset.ihe.ProvideAndRegisterDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.rim.RegistryResponseType;
import org.dcm4chee.xds2.infoset.util.DocumentRepositoryPortTypeFactory;
import org.dcm4chee.xds2.infoset.util.XDSDocumentAttachmentHandler;
import org.dcm4chee.xds2.infoset.ws.repository.DocumentRepositoryPortType;
import org.dcm4chee.xds2.infoset.ws.repository.DocumentRepositoryService;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.handler.AuthenticationHandler;
import org.openmrs.module.xdssender.api.handler.XdsDocumentMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;

@Component("xdssender.XdsSender")
public class XdsSender {
	
	private static final String URN_IHE_ITI = null;

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

		DocumentRepositoryPortTypeFactory.addHandler((BindingProvider)port, new XDSDocumentAttachmentHandler());
		
		((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
		    config.getXdsRepositoryUsername());
		((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
		    config.getXdsRepositoryPassword());
		return port.documentRepositoryProvideAndRegisterDocumentSetB(request);
	}

	public RegistryResponseType sendProvideRegisterClient2(ProvideAndRegisterDocumentSetRequestType request) {

		URL WSDL_LOCATION = DocumentRepositoryService.class.getResource("/wsdl/XDS.b_DocumentRepository.wsdl");
    
		Service service = Service.create(WSDL_LOCATION, new QName(URN_IHE_ITI, "DocumentRepository_Service"));

		DocumentRepositoryPortType port = service.getPort(DocumentRepositoryPortType.class);

		BindingProvider bp = (BindingProvider) port;
		
		SOAPBinding binding = (SOAPBinding) bp.getBinding();
		
		binding.setMTOMEnabled(false);
		
		bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
		    config.getXdsRepositoryUsername());
		bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
		    config.getXdsRepositoryPassword());
		
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getXdsRepositoryEndpoint());

		return port.documentRepositoryProvideAndRegisterDocumentSetB(request);
		
	}
}
