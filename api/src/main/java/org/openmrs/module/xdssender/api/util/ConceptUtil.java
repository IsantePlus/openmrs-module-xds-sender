package org.openmrs.module.xdssender.api.util;

import org.openmrs.ConceptNumeric;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component("xdssender.ConceptUtil")
public class ConceptUtil {

    private Map<String, String> openMrsUcumUnitMap = new HashMap<String, String>();

    @PostConstruct
    public void init() {
        openMrsUcumUnitMap.put("wks", "wk");
        openMrsUcumUnitMap.put("mmHg", "mm[Hg]");
        openMrsUcumUnitMap.put("months", "mon");
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
}
