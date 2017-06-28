package org.openmrs.module.xdssender.api.util;

import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.openmrs.Encounter;
import org.openmrs.Patient;

/**
 * Represents a document builder which can construct a clinical document of a particular type
 * 
 * @author JustinFyfe
 */
public interface DocumentBuilder {
	
	/**
	 * Get the classcode
	 * 
	 * @return
	 */
	public String getTypeCode();
	
	/**
	 * Get the format code
	 * 
	 * @return
	 */
	public String getFormatCode();
	
	/**
	 * Get the encounter event
	 */
	public Encounter getEncounterEvent();
	
	/**
	 * Set the Encounter this document builder will be representing
	 */
	public void setEncounterEvent(Encounter enc);
	
	/**
	 * Get the record target of this document
	 * 
	 * @return
	 */
	public Patient getRecordTarget();
	
	/**
	 * Set the record target of this document
	 * 
	 * @param recordTarget
	 */
	public void setRecordTarget(Patient recordTarget);
	
	/**
	 * Generate the document
	 * 
	 * @return
	 */
	ClinicalDocument generate(Section... sections);
	
}
