package org.openmrs.module.xdssender.api.xds;

import org.dcm4chee.xds2.common.exception.XDSException;
import org.dcm4chee.xds2.infoset.ihe.RetrieveDocumentSetRequestType;
import org.dcm4chee.xds2.infoset.ihe.RetrieveDocumentSetResponseType;
import org.dcm4chee.xds2.infoset.util.DocumentRepositoryPortTypeFactory;
import org.dcm4chee.xds2.infoset.ws.repository.DocumentRepositoryPortType;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("xdssender.XdsReceiver")
public class XdsReceiver {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private XdsSenderConfig config;
	
	public RetrieveDocumentSetResponseType sendRetrieveCCD(RetrieveDocumentSetRequestType retrieveRequest)
	        throws XDSException {
		DocumentRepositoryPortType port = DocumentRepositoryPortTypeFactory.getDocumentRepositoryPortSoap12(config
		        .getXdsRepositoryEndpoint());
		
		try {
			return port.documentRepositoryRetrieveDocumentSet(retrieveRequest);
		}
		catch (Exception e) {
			throw new XDSException("500", e.getMessage(), e);
		}
	}
}
