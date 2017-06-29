package org.openmrs.module.xdssender.api.cda.entry.impl;

import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalStatement;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Observation;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Obs;
import org.openmrs.module.shr.cdahandler.CdaHandlerConstants;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

/**
 * Simple observation entry builder
 * 
 * @author JustinFyfe
 */
@Component("xdssender.SimpleObservationEntryBuilder")
public class SimpleObservationEntryBuilder extends EntryBuilderImpl {
	
	/**
	 * Create the simple observation
	 */
	public Observation generate(CD<String> code, Obs obs) {
		return this.createObservation(Collections.singletonList(CdaHandlerConstants.ENT_TEMPLATE_SIMPLE_OBSERVATION), code,
		    obs);
	}
	
	/**
	 * Generate the simple observation
	 */
	public ClinicalStatement generate(BaseOpenmrsData data) {
		if (data instanceof Obs) {
			Obs obs = (Obs) data;
			CD<String> code = getCdaMetadataUtil().getStandardizedCode(obs.getConcept(),
			    CdaHandlerConstants.CODE_SYSTEM_LOINC, CD.class);
			if (code == null) // Get SNOMED as an alternate
				code = getCdaMetadataUtil().getStandardizedCode(obs.getConcept(), CdaHandlerConstants.CODE_SYSTEM_SNOMED,
				    CD.class);
			
			return this.generate(code, obs);
		}
		throw new IllegalArgumentException("data must be Obs");
	}
	
}
