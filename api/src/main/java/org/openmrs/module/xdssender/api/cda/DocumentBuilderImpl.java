package org.openmrs.module.xdssender.api.cda;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.marc.everest.datatypes.BL;
import org.marc.everest.datatypes.II;
import org.marc.everest.datatypes.NullFlavor;
import org.marc.everest.datatypes.TS;
import org.marc.everest.datatypes.generic.CE;
import org.marc.everest.datatypes.generic.CS;
import org.marc.everest.datatypes.generic.LIST;
import org.marc.everest.datatypes.generic.SET;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.AssignedCustodian;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Author;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Component2;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Component3;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Custodian;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.DocumentationOf;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Performer1;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.Section;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ServiceEvent;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.StructuredBody;
import org.marc.everest.rmim.uv.cdar2.vocabulary.ActRelationshipHasComponent;
import org.marc.everest.rmim.uv.cdar2.vocabulary.BindingRealm;
import org.marc.everest.rmim.uv.cdar2.vocabulary.ContextControl;
import org.marc.everest.rmim.uv.cdar2.vocabulary.ParticipationFunction;
import org.marc.everest.rmim.uv.cdar2.vocabulary.x_BasicConfidentialityKind;
import org.marc.everest.rmim.uv.cdar2.vocabulary.x_ServiceEventPerformer;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.XdsSenderConstants;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

/**
 * A generic clinical document builder which does not assign any template information
 * 
 * @author JustinFyfe
 */
public class DocumentBuilderImpl implements DocumentBuilder {
	
	private XdsSenderConfig config = XdsSenderConfig.getInstance();
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	private Patient recordTarget;
	
	private Encounter encounter;
	
	private CdaDataUtil cdaDataUtil = CdaDataUtil.getInstance();
	
	public DocumentBuilderImpl() {
	}
	
	public DocumentBuilderImpl(Patient recordTarget, Encounter encounter, CdaDataUtil cdaDataUtil) {
		this.recordTarget = recordTarget;
		this.encounter = encounter;
		this.cdaDataUtil = cdaDataUtil;
	}
	
	/**
	 * Get the document type code
	 */
	public String getTypeCode() {
		return "34133-9";
	}
	
	/**
	 * Get the document format code
	 */
	public String getFormatCode() {
		return "CDAR2/IHE 1.0";
	}
	
	/**
	 * Get the encounter event
	 */
	public Encounter getEncounterEvent() {
		return this.encounter;
	}
	
	/**
	 * Set the Encounter this document builder will be representing
	 */
	public void setEncounterEvent(Encounter enc) {
		this.encounter = enc;
	}
	
	/**
	 * Gets the currently assigned record target
	 */
	public Patient getRecordTarget() {
		return this.recordTarget;
	}
	
	/**
	 * Sets the record target
	 */
	public void setRecordTarget(Patient recordTarget) {
		this.recordTarget = recordTarget;
	}
	
	/**
	 * Generate the CDA
	 */
	public ClinicalDocument generate(Location location, Section... sections) {
		try {
			ClinicalDocument retVal = new ClinicalDocument();
			retVal.setTypeId(new II("2.16.840.1.113883.1.3", "POCD_HD000040"));
			retVal.setCode(this.getTypeCode());
			retVal.setRealmCode(SET.createSET(new CS<BindingRealm>(BindingRealm.UniversalRealmOrContextUsedInEveryInstance)));
			retVal.setTemplateId(LIST.createLIST(new II(XdsSenderConstants.DOC_TEMPLATE_MEDICAL_DOCUMENTS)));
			// Identifier is the SHR root of the odd document ODD ID + Current Time (making the UUID of the ODD)
			TS idDate = TS.now();
			idDate.setDateValuePrecision(TS.SECONDNOTIMEZONE);
			
			// Set core properties
			setDocumentId(retVal);
			retVal.setEffectiveTime(TS.now());
			
			// Set to Normal, anything above a normal will not be included in the extract
			retVal.setConfidentialityCode(new CE<x_BasicConfidentialityKind>(x_BasicConfidentialityKind.Normal));
			retVal.setLanguageCode(Context.getLocale().toString()); // CONF-5
			
			// Custodian
			Custodian custodian = new Custodian();
			custodian.setAssignedCustodian(new AssignedCustodian());
			custodian.getAssignedCustodian().setRepresentedCustodianOrganization(
			    cdaDataUtil.getCustodianOrganization(location));
			retVal.setCustodian(custodian);
			
			// Create documentation of
			// TODO: Do we only need one of these for all events that occur in the CDA or one for each?
			ServiceEvent event = new ServiceEvent(new CS<String>("PCPR")); // CCD CONF-3 & CONF-2
			Date earliestRecord, lastRecord;
			
			// Assign data form the encounter
			if (encounter != null) {
				Visit visit = encounter.getVisit();
				if (visit != null) {
					earliestRecord = visit.getStartDatetime();
					lastRecord = visit.getStopDatetime();
				} else {
					lastRecord = earliestRecord = encounter.getEncounterDatetime();
				}
				
				// Now add participants
				for (Entry<EncounterRole, Set<Provider>> encounterProvider : encounter.getProvidersByRoles().entrySet()) {
					
					for (Provider pvdr : encounterProvider.getValue()) {
						Author aut = new Author(ContextControl.OverridingPropagating);
						aut.setTime(new TS());
						aut.getTime().setNullFlavor(NullFlavor.NoInformation);
						aut.setAssignedAuthor(cdaDataUtil.createAuthorPerson(pvdr));
						retVal.getAuthor().add(aut);
					}
					for (Provider pvdr : encounterProvider.getValue()) {
						Performer1 performer = new Performer1(x_ServiceEventPerformer.PRF,
								cdaDataUtil.createAssignedEntity(pvdr));
						performer.setFunctionCode((CE<ParticipationFunction>) cdaDataUtil.parseCodeFromString(
							encounterProvider.getKey().getDescription(), CE.class));
						event.getPerformer().add(performer);
					}
				}
			} else {
				earliestRecord = recordTarget.getDateCreated();
				lastRecord = recordTarget.getDateChanged();
				
				Person person = Context.getAuthenticatedUser().getPerson();
				Collection<Provider> provider = Context.getProviderService().getProvidersByPerson(person);
				if (provider.size() > 0) {
					Author aut = new Author(ContextControl.OverridingPropagating);
					aut.setTime(new TS());
					aut.getTime().setNullFlavor(NullFlavor.NoInformation);
					aut.setAssignedAuthor(cdaDataUtil.createAuthorPerson(provider.iterator().next()));
					retVal.getAuthor().add(aut);
				}
				
			}
			
			// Set the effective time of records
			Calendar earliestCal = Calendar.getInstance(), latestCal = Calendar.getInstance();
			earliestCal.setTime(earliestRecord);
			
			if (lastRecord != null)
				latestCal.setTime(lastRecord);
			event.setEffectiveTime(new TS(earliestCal), new TS(latestCal)); // CCD CONF-4
			
			// Documentation of
			retVal.getDocumentationOf().add(new DocumentationOf(event));
			
			// Record target
			retVal.getRecordTarget().add(cdaDataUtil.createRecordTarget(recordTarget));
			
			// NOK (those within the time covered by this document)
			for (Relationship relatedPerson : Context.getPersonService().getRelationshipsByPerson(recordTarget)) {
				// Periodic hull
				retVal.getParticipant().add(cdaDataUtil.createRelatedPerson(relatedPerson, recordTarget));
			}
			
			retVal.setComponent(new Component2(ActRelationshipHasComponent.HasComponent, BL.TRUE, new StructuredBody()));
			for (Section sct : sections) {
				if (sct == null) {
					continue;
				}
				
				retVal.getComponent().getBodyChoiceIfStructuredBody().getComponent()
				        .add(new Component3(ActRelationshipHasComponent.HasComponent, BL.TRUE, sct));
				
				// Minify authors
				for (Author aut : sct.getAuthor()) {
					if (!cdaDataUtil.containsAuthor(retVal.getAuthor(), aut)) {
						retVal.getAuthor().add(aut);
					}
					
				}
				sct.getAuthor().clear();
			}
			
			return retVal;
		}
		catch (Exception e) {
			log.error(e);
			log.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException(e);
		}
	}
	
	private void setDocumentId(ClinicalDocument document) {
		if (encounter != null && encounter.getVisit() != null) {
			document.setId("visit/encounter", encounter.getVisit().getUuid() + "/" + encounter.getUuid());
		} else {
			document.setId(UUID.randomUUID());
		}
	}
}
