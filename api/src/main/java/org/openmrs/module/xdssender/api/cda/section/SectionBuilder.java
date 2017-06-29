package org.openmrs.module.xdssender.api.cda.section;

import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Entry;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;

/**
 * Represents a CDA section builder
 * 
 * @author JustinFyfe
 */
public interface SectionBuilder {
	
	/**
	 * Generate the section. Note: If the section is not level 3 then entries may be used for the
	 * generation of the text
	 * 
	 * @return
	 */
	Section generate(Entry... entries);
}
