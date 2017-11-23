package org.openmrs.module.xdssender.api.cda;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.marc.everest.datatypes.generic.CD;
import org.marc.everest.formatters.xml.its1.XmlIts1Formatter;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Location;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
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
		if (builder.getEncounterEvent() == Context.getObsService().getObservationsByPerson(builder.getRecordTarget()))
			relevantObs = builder.getEncounterEvent().getAllObs();
		else
			relevantObs = Context.getObsService().getObservationsByPerson(builder.getRecordTarget());
		
		for (Obs obs : relevantObs) {
			CD<String> loincCode = metadataUtil.getStandardizedCode(obs.getConcept(), XdsSenderConstants.CODE_SYSTEM_LOINC,
			    CD.class);
			int conceptId = obs.getConcept().getId();
			// EDD Stuff
			if (obs.getConcept().getId().equals(XdsSenderConstants.CONCEPT_ID_MEDICATION_HISTORY))
				medicationObs.add(obs);
			else if ((conceptId == 5596 || loincCode.getCode() != null && loincCode.getCode().equals("11778-8"))
			        && (estimatedDeliveryDateObs == null || obs.getDateCreated().after(
			            estimatedDeliveryDateObs.getDateCreated())))
				estimatedDeliveryDateObs = obs;
			else if (conceptId == 1427
			        || loincCode.getCode() != null
			        && loincCode.getCode().equals("8655-2")
			        && (lastMenstrualPeriodObs == null || obs.getDateCreated()
			                .after(lastMenstrualPeriodObs.getDateCreated())))
				lastMenstrualPeriodObs = obs;
			else if (loincCode.getCode() != null && loincCode.getCode().equals("8348-5")
			        && (prepregnancyWeightObs == null || obs.getDateCreated().after(prepregnancyWeightObs.getDateCreated())))
				prepregnancyWeightObs = obs;
			else if ((conceptId == 1438 || loincCode.getCode() != null && loincCode.getCode().equals("11884-4"))
			        && (gestgationalAgeObs == null || obs.getDateCreated().after(gestgationalAgeObs.getDateCreated())))
				gestgationalAgeObs = obs;
			else if (conceptId == 1439 || loincCode.getCode() != null && loincCode.getCode().equals("11881-0")
			        && (fundalHeightObs == null || obs.getDateCreated().after(fundalHeightObs.getDateCreated())))
				fundalHeightObs = obs;
			else if ((conceptId == 5085 || loincCode.getCode() != null && loincCode.getCode().equals("8480-6"))
			        && (systolicBpObs == null || obs.getDateCreated().after(systolicBpObs.getDateCreated())))
				systolicBpObs = obs;
			else if ((conceptId == 5086 || loincCode.getCode() != null && loincCode.getCode().equals("8462-4"))
			        && (diastolicBpObs == null || obs.getDateCreated().after(diastolicBpObs.getDateCreated())))
				diastolicBpObs = obs;
			else if ((conceptId == 5089 || loincCode.getCode() != null && loincCode.getCode().equals("3141-9"))
			        && (weightObs == null || obs.getDateCreated().after(weightObs.getDateCreated())))
				weightObs = obs;
			else if (loincCode.getCode() != null && loincCode.getCode().equals("11876-0")
			        && (presentationObs == null || obs.getDateCreated().after(presentationObs.getDateCreated())))
				presentationObs = obs;
			else if ((conceptId == 5090 || loincCode.getCode() != null && loincCode.getCode().equals("8302-2"))
			        && (heightObs == null || obs.getDateCreated().after(heightObs.getDateCreated())))
				heightObs = obs;
			else if ((conceptId == 5088 || loincCode.getCode() != null && loincCode.getCode().equals("8310-5"))
			        && (temperatureObs == null || obs.getDateCreated().after(temperatureObs.getDateCreated())))
				temperatureObs = obs;
			
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
		
		/*		Problem[] problems = Context.getActiveListService()
				        .getActiveListItems(builder.getRecordTarget(), Problem.ACTIVE_LIST_TYPE).toArray(new Problem[] {});
				Allergy[] allergies = Context.getActiveListService()
				        .getActiveListItems(builder.getRecordTarget(), Allergy.ACTIVE_LIST_TYPE).toArray(new Allergy[] {});*//*

	   if (problems.length > 0)
	   probSection = probBuilder.generate(problems);

	   if (allergies.length > 0)
	   allergySection = allergyBuilder.generate(allergies);*/

		org.openmrs.Location patientLocation=null;

		for (PatientIdentifier pid : patient.getIdentifiers()) {
			patientLocation = pid.getLocation();
		}

		// Formatter
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ClinicalDocument doc = builder.generate(patientLocation, eddSection, flowsheetSection, vitalSignsSection, medicationsSection, probSection,
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
