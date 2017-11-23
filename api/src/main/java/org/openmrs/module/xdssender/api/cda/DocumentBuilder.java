package org.openmrs.module.xdssender.api.cda;

import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;

/**
 * Represents a document builder which can construct a clinical document of a particular type
 * 
 * @author JustinFyfe
 */
public interface DocumentBuilder {
	
	/**
	 * Get the classcode
	 */
	String getTypeCode();
	
	/**
	 * Get the format code
	 */
	String getFormatCode();
	
	/**
	 * Get the encounter event
	 */
	Encounter getEncounterEvent();
	
	/**
	 * Set the Encounter this document builder will be representing
	 */
	void setEncounterEvent(Encounter enc);
	
	/**
	 * Get the record target of this document
	 */
	Patient getRecordTarget();
	
	/**
	 * Set the record target of this document
	 */
	void setRecordTarget(Patient recordTarget);
	
	/**
	 * Generate the document
	 */
	ClinicalDocument generate(Location location, Section... sections);
	
}
