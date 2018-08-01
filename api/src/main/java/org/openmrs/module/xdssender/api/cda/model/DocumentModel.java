package org.openmrs.module.xdssender.api.cda.model;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Author;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public final class DocumentModel {
	
	private String html;
	
	private ClinicalDocument doc;
	
	private String formatCode;
	
	private String typeCode;

	private String typeCodeScheme;
	
	private byte[] data;

	private List<Author> authors;
	
	private static Log log = LogFactory.getLog(DocumentModel.class);
	
	/**
	 * Can only be created by static method
	 */
	private DocumentModel() {
		
	}
	
	public ClinicalDocument getDoc() {
		return doc;
	}
	
	public String getFormatCode() {
		return formatCode;
	}
	
	public String getTypeCode() {
		return typeCode;
	}

	public String getTypeCodeScheme() {
		return typeCodeScheme;
	}
	
	public byte[] getData() {
		return data;
	}

	public List<Author> getAuthors() {
		return authors != null ? authors : new ArrayList<Author>();
	}

	public static DocumentModel createInstance(byte[] documentData) {
		return createInstance(documentData, null, null, null, (ClinicalDocument)null);
	}
	
	public static DocumentModel createInstance(byte[] documentData, String typeCode, String typeCodeScheme,
											   String formatCode, ClinicalDocument doc) {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(documentData);
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(DocumentModel.class.getClassLoader().getResourceAsStream("cda.xsl"));
			Transformer transformer = factory.newTransformer(xslt);
			
			Source text = new StreamSource(in);
			StringWriter sw = new StringWriter();
			transformer.transform(text, new StreamResult(sw));
			DocumentModel retVal = new DocumentModel();
			retVal.html = sw.toString();
			retVal.applyFormatting();
			log.error(retVal.html);
			retVal.typeCode = typeCode;
			retVal.typeCodeScheme = typeCodeScheme;
			retVal.formatCode = formatCode;
			retVal.doc = doc;
			retVal.data = documentData;
			retVal.authors = doc.getAuthor();
			return retVal;
		}
		catch (TransformerException e) {
			log.error(e);
			return null;
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static DocumentModel createInstance(byte[] documentData, String typeCode, String typeCodeScheme,
											   String formatCode, String msg, List<Author> authors) {
		DocumentModel retVal = new DocumentModel();
		retVal.html = msg;
		retVal.typeCode = typeCode;
		retVal.typeCodeScheme = typeCodeScheme;
		retVal.formatCode = formatCode;
		retVal.data = documentData;
		retVal.authors = authors;
		return retVal;
	}
	
	public void applyFormatting() {
		html = html.substring(html.indexOf("<body>") + "<body>".length());
		html = html.substring(0, html.indexOf("</body>"));
	}
	
	public String getHtml() {
		return html;
	}
	
}
