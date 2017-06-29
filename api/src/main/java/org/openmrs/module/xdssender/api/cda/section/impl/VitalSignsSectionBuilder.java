package org.openmrs.module.xdssender.api.cda.section.impl;

import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.generic.CE;
import org.marc.everest.datatypes.generic.LIST;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Entry;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.marc.everest.rmim.uv.cdar2.vocabulary.x_ActRelationshipEntry;
import org.openmrs.Obs;
import org.openmrs.module.shr.cdahandler.CdaHandlerConstants;
import org.openmrs.module.xdssender.api.cda.entry.impl.VitalSignsBatteryEntryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Vital signs section builder
 * 
 * @author JustinFyfe
 */
@Component("xdssender.VitalSignsSectionBuilder")
public class VitalSignsSectionBuilder extends SectionBuilderImpl {
	
	@Autowired
	private VitalSignsBatteryEntryBuilder batteryBuilder;
	
	/**
	 * Generate the vital signs section
	 */
	@Override
	public Section generate(Entry... entries) {
		Section retVal = super.generate(entries);
		retVal.setTemplateId(LIST.createLIST(new II(CdaHandlerConstants.SCT_TEMPLATE_CCD_VITAL_SIGNS), new II(
		        CdaHandlerConstants.SCT_TEMPLATE_VITAL_SIGNS)));
		retVal.setTitle("Vital Signs");
		retVal.setCode(new CE<String>("8716-3", CdaHandlerConstants.CODE_SYSTEM_LOINC,
		        CdaHandlerConstants.CODE_SYSTEM_NAME_LOINC, null, "VITAL SIGNS", null));
		return retVal;
	}
	
	/**
	 * Generate vital signs section with the specified data
	 */
	public Section generate(Obs systolicBpObs, Obs diastolicBpObs, Obs weightObs, Obs heightObs, Obs temperatureObs) {
		Entry vitalSignsBattery = new Entry(x_ActRelationshipEntry.HasComponent, BL.TRUE, batteryBuilder.generate(
		    systolicBpObs, diastolicBpObs, weightObs, heightObs, temperatureObs));
		return this.generate(vitalSignsBattery);
	}
}
