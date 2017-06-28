package org.openmrs.module.xdssender.api.cda;

import org.openmrs.ConceptNumeric;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component("xdssender.ConceptUtil")
public class ConceptUtil {
	
	private Map<String, String> openMrsUcumUnitMap = new HashMap<String, String>();
	
	private Map<String, String> conceptSourceNameMap = new HashMap<String, String>();
	
	@PostConstruct
	public void init() {
		openMrsUcumUnitMap.put("wks", "wk");
		openMrsUcumUnitMap.put("mmHg", "mm[Hg]");
		openMrsUcumUnitMap.put("months", "mon");
		
		conceptSourceNameMap.put("LOINC", XdsSenderConstants.CODE_SYSTEM_LOINC);
		conceptSourceNameMap.put("SNOMED CT", XdsSenderConstants.CODE_SYSTEM_SNOMED);
		conceptSourceNameMap.put("CIEL", XdsSenderConstants.CODE_SYSTEM_CIEL);
		conceptSourceNameMap.put("HL-7 CVX", XdsSenderConstants.CODE_SYSTEM_CVX);
		conceptSourceNameMap.put("RxNORM", XdsSenderConstants.CODE_SYSTEM_RXNORM);
		conceptSourceNameMap.put("ICD-10-WHO", XdsSenderConstants.CODE_SYSTEM_ICD_10);
	}
	
	/**
	 * Get the ucum code from the openMRS numeric concept's units
	 */
	public String getUcumUnitCode(ConceptNumeric numericConcept) {
		String mappedUnit = numericConcept.getUnits();
		if (openMrsUcumUnitMap.containsKey(mappedUnit)) {
			mappedUnit = openMrsUcumUnitMap.get(mappedUnit);
		}
		return mappedUnit;
	}
	
	public String mapConceptSourceNameToOid(String name) {
		String oid = conceptSourceNameMap.get(name);
		if (oid != null) {
			return oid;
		}
		return name;
	}
	
	public String mapOidToConceptSourceName(String oid) {
		for (Map.Entry<String, String> ent : conceptSourceNameMap.entrySet()) {
			if (ent.getValue().equals(oid)) {
				return ent.getKey();
			}
		}
		return oid;
	}
}
