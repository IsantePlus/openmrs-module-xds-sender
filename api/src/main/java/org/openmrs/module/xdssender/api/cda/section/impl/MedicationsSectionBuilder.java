package org.openmrs.module.xdssender.api.cda.section.impl;

import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.generic.CE;
import org.marc.everest.datatypes.generic.LIST;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Entry;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.marc.everest.rmim.uv.cdar2.vocabulary.x_ActRelationshipEntry;
import org.openmrs.Obs;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.cda.entry.impl.MedicationEntryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Section builder for medications
 * 
 * @author JustinFyfe
 */
@Component("xdssender.MediccationsSectionBuilder")
public class MedicationsSectionBuilder extends SectionBuilderImpl {
	
	@Autowired
	private MedicationEntryBuilder administrationBuilder;
	
	/**
	 * Generate the medications section
	 */
	@Override
	public Section generate(Entry... entries) {
		
		Section retVal = super.generate(entries);
		retVal.setTemplateId(LIST.createLIST(new II(XdsSenderConstants.SCT_TEMPLATE_CCD_MEDICATIONS), new II(
		        XdsSenderConstants.SCT_TEMPLATE_MEDICATIONS)));
		retVal.setTitle("History of Medication Use");
		retVal.setCode(new CE<String>("10160-0", XdsSenderConstants.CODE_SYSTEM_LOINC,
		        XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null, "HISTORY OF MEDICATION USE", null));
		return retVal;
	}
	
	/**
	 * Generate the section data from a series of medication history observations
	 */
	public Section generate(Obs... medicationHistoryObs) {
		List<Entry> entries = new ArrayList<Entry>();
		if (medicationHistoryObs.length == 0)
			entries.add(new Entry(x_ActRelationshipEntry.HasComponent, BL.TRUE, administrationBuilder.generateUnknown()));
		else
			for (Obs med : medicationHistoryObs)
				entries.add(new Entry(x_ActRelationshipEntry.HasComponent, BL.TRUE, administrationBuilder.generate(med)));
		
		return this.generate(entries.toArray(new Entry[] {}));
		
	}
	
}
