package org.openmrs.module.xdssender.api.cda.section.impl;

import org.marc.everest.datatypes.SD;
import org.marc.everest.datatypes.doc.StructDocElementNode;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalStatement;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Entry;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.marc.everest.rmim.uv.cdar2.vocabulary.x_ActRelationshipEntry;
import org.openmrs.module.xdssender.api.cda.CdaDataUtil;
import org.openmrs.module.xdssender.api.cda.CdaTextUtil;
import org.openmrs.module.xdssender.api.cda.section.SectionBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Generic clinical section builder
 * 
 * @author JustinFyfe
 */
public abstract class SectionBuilderImpl implements SectionBuilder {
	
	@Autowired
	private CdaTextUtil cdaTextUtil;
	
	@Autowired
	private CdaDataUtil cdaDataUtil;
	
	/**
	 * Generate the Level 3 content text
	 */
	protected SD generateLevel3Text(Section section) {
		SD retVal = new SD();
		Class<? extends ClinicalStatement> previousStatementType = null;
		StructDocElementNode context = null;
		for (Entry ent : section.getEntry()) {
			// Is this different than the previous?
			if (!ent.getClinicalStatement().getClass().equals(previousStatementType)) {
				// Add existing context node before generating another
				if (context != null)
					retVal.getContent().add(context);
				// Force the generation of new context
				context = null;
			}
			StructDocElementNode genNode = this.cdaTextUtil.generateText(ent.getClinicalStatement(), context);
			
			// Set the context node
			if (context == null)
				context = genNode;
			previousStatementType = ent.getClinicalStatement().getClass();
			
			ent.setTypeCode(x_ActRelationshipEntry.DRIV);
		}
		if (context != null && !retVal.getContent().contains(context))
			retVal.getContent().add(context);
		return retVal;
	}
	
	/**
	 * Generate section
	 */
	public Section generate(Entry... entries) {
		Section retVal = new Section();
		retVal.setId(UUID.randomUUID());
		
		for (Entry ent : entries) {
			retVal.getEntry().add(ent);
		}
		
		// Generate the text
		retVal.setText(this.generateLevel3Text(retVal));
		
		return retVal;
	}
	

	protected CdaTextUtil getCdaTextUtil() {
		return cdaTextUtil;
	}
	
	protected CdaDataUtil getCdaDataUtil() {
		return cdaDataUtil;
	}
}
