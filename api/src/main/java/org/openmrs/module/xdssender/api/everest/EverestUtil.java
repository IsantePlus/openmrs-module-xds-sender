package org.openmrs.module.xdssender.api.everest;

import org.marc.everest.datatypes.PQ;
import org.marc.everest.datatypes.generic.SXCM;
import org.marc.everest.formatters.xml.datatypes.r1.DatatypeFormatter;
import org.marc.everest.formatters.xml.datatypes.r1.R1FormatterCompatibilityMode;
import org.marc.everest.formatters.xml.its1.XmlIts1Formatter;
import org.marc.everest.interfaces.IGraphable;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;
import org.marc.everest.util.SimpleSiUnitConverter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EverestUtil {
	
	private static final Map<String, Class<? extends IGraphable>> extendedTypes = new HashMap<String, Class<? extends IGraphable>>() {
		
		{
			this.put("POCD_MT000040UV.PlayingEntity", SdtcPlayingEntity.class);
			this.put("POCD_MT000040UV.SubjectPerson", SdtcSubjectPerson.class);
			this.put("org.marc.everest.datatypes.interfaces.ISetComponent", SXCM.class);
		}
	};
	
	private EverestUtil() {
	}
	
	public static XmlIts1Formatter createFormatter() {
		XmlIts1Formatter formatter = new XmlIts1Formatter();
		formatter.getGraphAides().add(new DatatypeFormatter(R1FormatterCompatibilityMode.ClinicalDocumentArchitecture));
		formatter.addCachedClass(ClinicalDocument.class);
		Iterator var1 = extendedTypes.entrySet().iterator();
		
		while (var1.hasNext()) {
			Map.Entry<String, Class<? extends IGraphable>> entry = (Map.Entry) var1.next();
			formatter.registerXSITypeName(entry.getKey(), entry.getValue());
		}
		
		formatter.setValidateConformance(false);
		if (PQ.getUnitConverters().size() == 0) {
			PQ.getUnitConverters().add(new SimpleSiUnitConverter());
		}
		
		return formatter;
	}
}
