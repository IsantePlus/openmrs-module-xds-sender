package org.openmrs.module.xdssender.api.cda.section.impl;

import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.generic.CE;
import org.marc.everest.datatypes.generic.LIST;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Entry;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.springframework.stereotype.Component;

/**
 * Active problems section bulder
 * 
 * @author JustinFyfe
 */
@Component("xdsSender.ActiveProblemsSectionBuilder")
public class ActiveProblemsSectionBuilder extends SectionBuilderImpl {
	
	/**
	 * Generate the active problems section
	 */
	@Override
	public Section generate(Entry... entries) {
		Section retVal = super.generate(entries);
		retVal.setTemplateId(LIST.createLIST(new II(XdsSenderConstants.SCT_TEMPLATE_ACTIVE_PROBLEMS), new II(
		        XdsSenderConstants.SCT_TEMPLATE_CCD_PROBLEM)));
		retVal.setTitle("Active Problems");
		retVal.setCode(new CE<String>("11450-4", XdsSenderConstants.CODE_SYSTEM_LOINC, "LOINC", null, "PROBLEM LIST", null));
		return retVal;
	}
}
