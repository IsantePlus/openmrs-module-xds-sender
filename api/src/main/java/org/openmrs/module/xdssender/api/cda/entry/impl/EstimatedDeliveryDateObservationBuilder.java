package org.openmrs.module.xdssender.api.cda.entry.impl;

import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalStatement;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.EntryRelationship;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Observation;
import org.marc.everest.rmim.uv.cdar2.vocabulary.x_ActRelationshipEntryRelationship;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Obs;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Estimated delivery date builder
 * 
 * @author JustinFyfe
 */
@Component("xdssender.EstimatedDeliveryDateObservationBuilder")
public class EstimatedDeliveryDateObservationBuilder extends EntryBuilderImpl {
	
	/**
	 * Generate the delivery date
	 */
	public Observation generate(Obs estimatedDeliveryDateObs, Obs lastMenstrualPeriodObs) {
		
		if (estimatedDeliveryDateObs.getValueDate() == null)
			throw new IllegalArgumentException("estimatedDeliveryDateObs must carry Date value");
		else if (lastMenstrualPeriodObs.getValueDate() == null)
			throw new IllegalArgumentException("lastMenstrualPeriodObs must carry Date value");
		
		Observation deliveryDateObs = super.createObservation(Arrays
		        .asList(XdsSenderConstants.ENT_TEMPLATE_SIMPLE_OBSERVATION), new CD<String>("11779-6",
		        XdsSenderConstants.CODE_SYSTEM_LOINC, XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null,
		        "Delivery date estimated from date of last menstrual period", null), estimatedDeliveryDateObs), eddObs = super
		        .createObservation(Arrays.asList(XdsSenderConstants.ENT_TEMPLATE_SIMPLE_OBSERVATION,
		            XdsSenderConstants.ENT_TEMPLATE_DELIVERY_DATE_OBSERVATION), new CD<String>("11778-8",
		                XdsSenderConstants.CODE_SYSTEM_LOINC, XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null,
		                "Delivery Date - Estimate", null), estimatedDeliveryDateObs), lmpObs = super.createObservation(
		    Arrays.asList(XdsSenderConstants.ENT_TEMPLATE_SIMPLE_OBSERVATION), new CD<String>("8655-2",
		            XdsSenderConstants.CODE_SYSTEM_LOINC, XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null,
		            "Date of last menstrual period - reported", null), lastMenstrualPeriodObs);
		eddObs.getEntryRelationship().add(
		    new EntryRelationship(x_ActRelationshipEntryRelationship.SPRT, BL.TRUE, deliveryDateObs));
		deliveryDateObs.getEntryRelationship().add(
		    new EntryRelationship(x_ActRelationshipEntryRelationship.SPRT, BL.TRUE, lmpObs));
		return eddObs;
	}
	
	/**
	 * Generate
	 */
	public ClinicalStatement generate(BaseOpenmrsData data) {
		
		if (data instanceof Obs)
			return this.generate((Obs) data, ((Obs) data).getGroupMembers().iterator().next());
		throw new IllegalArgumentException("data must be Obs");
	}
	
}
