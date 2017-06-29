package org.openmrs.module.xdssender.api.cda.entry.impl;

import org.apache.commons.lang.NotImplementedException;
import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalStatement;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Component4;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Organizer;
import org.marc.everest.rmim.uv.cdar2.vocabulary.ActRelationshipHasComponent;
import org.marc.everest.rmim.uv.cdar2.vocabulary.ActStatus;
import org.marc.everest.rmim.uv.cdar2.vocabulary.x_ActClassDocumentEntryOrganizer;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Vital signs battery entry builder
 * 
 * @author JustinFyfe
 */
@Component("xdssender.VitalSignsBatteryEntryBuilder")
public class VitalSignsBatteryEntryBuilder extends EntryBuilderImpl {
	
	/**
	 * Generate the clincal statement from an encounter
	 */
	public ClinicalStatement generate(BaseOpenmrsData data) {
		throw new NotImplementedException();
	}
	
	/**
	 * Create the organizer from the discrete obs
	 */
	public ClinicalStatement generate(Obs systolicBpObs, Obs diastolicBpObs, Obs weightObs, Obs heightObs, Obs temperatureObs) {
		Encounter batteryEnc = systolicBpObs.getEncounter();
		
		if (heightObs != null && !batteryEnc.getId().equals(heightObs.getEncounter().getId())
		        || !batteryEnc.getId().equals(systolicBpObs.getEncounter().getId()) || diastolicBpObs != null
		        && !batteryEnc.getId().equals(diastolicBpObs.getEncounter().getId()) || temperatureObs != null
		        && !batteryEnc.getId().equals(temperatureObs.getEncounter().getId()) || weightObs != null
		        && !batteryEnc.getId().equals(weightObs.getEncounter().getId()))
			throw new IllegalArgumentException("All arguments for the flowsheet panel must come from the same encounter");
		
		Organizer batteryOrganizer = super.createOrganizer(x_ActClassDocumentEntryOrganizer.BATTERY, Arrays
		        .asList(XdsSenderConstants.ENT_TEMPLATE_VITAL_SIGNS_ORGANIZER,
		            XdsSenderConstants.ENT_TEMPLATE_CCD_VITAL_SIGNS_ORGANIZER), new CD<String>("46680005",
		        XdsSenderConstants.CODE_SYSTEM_SNOMED, "SNOMED CT", null, "Vital Signs", null), new II(getConfiguration()
		        .getEncounterRoot(), batteryEnc.getId().toString()), ActStatus.Completed, batteryEnc.getEncounterDatetime());
		
		SimpleObservationEntryBuilder obsBuilder = new SimpleObservationEntryBuilder();
		
		batteryOrganizer.getComponent().add(
		    new Component4(ActRelationshipHasComponent.HasComponent, BL.TRUE, obsBuilder.generate(new CD<String>("8480-6",
		            XdsSenderConstants.CODE_SYSTEM_LOINC, XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null,
		            "Blood pressure - Systolic", null), systolicBpObs)));
		
		if (diastolicBpObs != null)
			batteryOrganizer.getComponent().add(
			    new Component4(ActRelationshipHasComponent.HasComponent, BL.TRUE, obsBuilder.generate(new CD<String>(
			            "8462-4", XdsSenderConstants.CODE_SYSTEM_LOINC, XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null,
			            "Blood pressure - Diastolic", null), diastolicBpObs)));
		if (weightObs != null)
			batteryOrganizer.getComponent().add(
			    new Component4(ActRelationshipHasComponent.HasComponent, BL.TRUE, obsBuilder.generate(new CD<String>(
			            "3141-9", XdsSenderConstants.CODE_SYSTEM_LOINC, XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null,
			            "Body weight measured", null), weightObs)));
		if (heightObs != null)
			batteryOrganizer.getComponent().add(
			    new Component4(ActRelationshipHasComponent.HasComponent, BL.TRUE, obsBuilder.generate(new CD<String>(
			            "8302-2", XdsSenderConstants.CODE_SYSTEM_LOINC, XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null,
			            "Body height measured", null), heightObs)));
		if (temperatureObs != null)
			batteryOrganizer.getComponent().add(
			    new Component4(ActRelationshipHasComponent.HasComponent, BL.TRUE, obsBuilder.generate(new CD<String>(
			            "8310-5", XdsSenderConstants.CODE_SYSTEM_LOINC, XdsSenderConstants.CODE_SYSTEM_NAME_LOINC, null,
			            "Body Temperature", null), temperatureObs)));
		
		return batteryOrganizer;
	}
	
}
