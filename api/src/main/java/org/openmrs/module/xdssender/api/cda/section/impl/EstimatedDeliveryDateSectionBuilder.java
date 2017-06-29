package org.openmrs.module.xdssender.api.cda.section.impl;

import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.generic.CE;
import org.marc.everest.datatypes.generic.LIST;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Entry;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Observation;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.marc.everest.rmim.uv.cdar2.vocabulary.x_ActRelationshipEntry;
import org.openmrs.Obs;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.cda.entry.impl.EstimatedDeliveryDateObservationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * EDD section builder
 * 
 * @author JustinFyfe
 */
@Component("xdssender.EstimatedDeliveryDateSectionBuilder")
public class EstimatedDeliveryDateSectionBuilder extends SectionBuilderImpl {
	
	@Autowired
	private EstimatedDeliveryDateObservationBuilder eddObsBuilder;
	
	@Override
	public Section generate(Entry... entries) {
		Section retVal = super.generate(entries);
		retVal.setTemplateId(LIST.createLIST(new II(XdsSenderConstants.SCT_TEMPLATE_ESTIMATED_DELIVERY_DATES)));
		retVal.setTitle("Estimated Date of Delivery");
		retVal.setCode(new CE<String>("57060-6", XdsSenderConstants.CODE_SYSTEM_LOINC,
		        XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null, "Estimated Date of Delivery", null));
		return retVal;
	}
	
	public Section generate(Obs estimatedDeliveryDateObs, Obs lastMenstrualPeriodObs) {
		
		if (estimatedDeliveryDateObs.getValueDate() == null)
			throw new IllegalArgumentException("estimatedDeliveryDateObs must carry Date value");
		else if (lastMenstrualPeriodObs.getValueDate() == null)
			throw new IllegalArgumentException("lastMenstrualPeriodObs must carry Date value");
		
		Observation eddObservation = eddObsBuilder.generate(estimatedDeliveryDateObs, lastMenstrualPeriodObs);
		
		Entry entry = new Entry(x_ActRelationshipEntry.HasComponent, BL.TRUE, eddObservation);
		return this.generate(entry);
		
	}
}
