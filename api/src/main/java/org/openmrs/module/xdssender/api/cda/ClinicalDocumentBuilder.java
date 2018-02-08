package org.openmrs.module.xdssender.api.cda;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.formatters.xml.its1.XmlIts1Formatter;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.XdsSenderConstants;
import org.openmrs.module.xdssender.api.cda.model.DocumentModel;
import org.openmrs.module.xdssender.api.cda.section.impl.ActiveProblemsSectionBuilder;
import org.openmrs.module.xdssender.api.cda.section.impl.AntepartumFlowsheetPanelSectionBuilder;
import org.openmrs.module.xdssender.api.cda.section.impl.EstimatedDeliveryDateSectionBuilder;
import org.openmrs.module.xdssender.api.cda.section.impl.MedicationsSectionBuilder;
import org.openmrs.module.xdssender.api.cda.section.impl.VitalSignsSectionBuilder;
import org.openmrs.module.xdssender.api.everest.EverestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component("xdsender.ClinicalDocumentBuilder")
public class ClinicalDocumentBuilder {
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private CdaMetadataUtil metadataUtil;
	
	@Autowired
	private EstimatedDeliveryDateSectionBuilder eddSectionBuilder;
	
	@Autowired
	private AntepartumFlowsheetPanelSectionBuilder flowsheetSectionBuilder;
	
	@Autowired
	private VitalSignsSectionBuilder vitalSignsSectionBuilder;
	
	@Autowired
	private MedicationsSectionBuilder medSectionBuilder;
	
	@Autowired
	private ActiveProblemsSectionBuilder probBuilder;
	
	public DocumentModel buildDocument(Patient patient, Encounter encounter) throws InstantiationException,
	        IllegalAccessException {
		
		DocumentBuilder builder = new DocumentBuilderImpl();
		
		builder.setRecordTarget(patient);
		builder.setEncounterEvent(encounter);
		
		Obs estimatedDeliveryDateObs = null, lastMenstrualPeriodObs = null, prepregnancyWeightObs = null, gestgationalAgeObs = null, fundalHeightObs = null, systolicBpObs = null, diastolicBpObs = null, weightObs = null, heightObs = null, presentationObs = null, temperatureObs = null;
		List<Obs> medicationObs = new ArrayList<Obs>();
		
		// Obs relevant to this encounter
		Collection<Obs> relevantObs = null;
		//if (builder.getEncounterEvent() == Context.getObsService().getObservationsByPerson(builder.getRecordTarget()))
			relevantObs = builder.getEncounterEvent().getAllObs();
		//else
			//relevantObs = Context.getObsService().getObservationsByPerson(builder.getRecordTarget());
		
		for (Obs obs : relevantObs) {
			//we want to have all obs groups at the end of the list
			if (obs.hasGroupMembers()) {
				medicationObs.add(obs);
			} else {
				medicationObs.add(0, obs);	//this probably is some group member
			}
		}
		
		Section eddSection = null, flowsheetSection = null, vitalSignsSection = null, medicationsSection = null, probSection = null, allergySection = null;
		
		if (estimatedDeliveryDateObs != null && lastMenstrualPeriodObs != null)
			eddSection = eddSectionBuilder.generate(estimatedDeliveryDateObs, lastMenstrualPeriodObs);
		
		if (gestgationalAgeObs != null && systolicBpObs != null && diastolicBpObs != null && weightObs != null)
			flowsheetSection = flowsheetSectionBuilder.generate(prepregnancyWeightObs, gestgationalAgeObs, fundalHeightObs,
			    presentationObs, systolicBpObs, diastolicBpObs, weightObs);
		
		if (systolicBpObs != null && diastolicBpObs != null && weightObs != null && heightObs != null
		        && temperatureObs != null)
			vitalSignsSection = vitalSignsSectionBuilder.generate(systolicBpObs, diastolicBpObs, weightObs, heightObs,
			    temperatureObs);
		
		medicationsSection = medSectionBuilder.generate(medicationObs.toArray(new Obs[] {}));


		Location visitLocation = Context.getLocationService().getDefaultLocation();;

		if(encounter.getVisit() != null)
			visitLocation = encounter.getVisit().getLocation();

		// Formatter
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ClinicalDocument doc = builder.generate(visitLocation, eddSection, flowsheetSection, vitalSignsSection, medicationsSection, probSection,
			    allergySection);

			XmlIts1Formatter formatter = EverestUtil.createFormatter();
			formatter.graph(baos, doc);

			return DocumentModel.createInstance(baos.toByteArray(), builder.getTypeCode(), builder.getFormatCode(), doc);
		} catch (Exception e) {
			log.error("Error generating document:", e);
			throw new RuntimeException(e);
		}
	}
}
