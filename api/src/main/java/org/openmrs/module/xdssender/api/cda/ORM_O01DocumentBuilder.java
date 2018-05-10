package org.openmrs.module.xdssender.api.cda;

import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;

@Component
public class ORM_O01DocumentBuilder {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	public DocumentModel buildDocument(String message) {
		try {
			byte[] data = message.getBytes(StandardCharsets.UTF_8);
			
			return DocumentModel.createInstance(data);
		}
		catch (Exception e) {
			log.error("Error generating document:", e);
			throw new RuntimeException(e);
		}
	}
}
