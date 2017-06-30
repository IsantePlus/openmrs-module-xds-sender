package org.openmrs.module.xdssender.api.everest;

import org.marc.everest.annotations.ConformanceType;
import org.marc.everest.annotations.Property;
import org.marc.everest.annotations.PropertyType;
import org.marc.everest.annotations.Structure;
import org.marc.everest.annotations.StructureType;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.PlayingEntity;

@Structure(name = "PlayingEntity", model = "POCD_MT000040", publisher = "HL7 International", structureType = StructureType.MESSAGETYPE)
public class SdtcPlayingEntity extends PlayingEntity {
	
	private SET<II> m_id;
	
	public SdtcPlayingEntity() {
	}
	
	@Property(name = "id", namespaceUri = "urn:hl7-org:sdtc", propertyType = PropertyType.NONSTRUCTURAL, conformance = ConformanceType.REQUIRED, minOccurs = 0, maxOccurs = -1)
	public SET<II> getId() {
		return this.m_id;
	}
	
	public void setId(SET<II> value) {
		this.m_id = value;
	}
	
}
