package org.openmrs.module.xdssender.api.errorhandling;

import java.io.Serializable;
import java.util.Objects;

public class ExportProvideAndRegisterParameters implements Serializable {
	
	private String patientUuid;
	
	private String encounterUuid;

	public ExportProvideAndRegisterParameters() {
	}

	public ExportProvideAndRegisterParameters(String patientUuid, String encounterUuid) {
		this.patientUuid = patientUuid;
		this.encounterUuid = encounterUuid;
	}
	
	public String getPatientUuid() {
		return patientUuid;
	}
	
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	public String getEncounterUuid() {
		return encounterUuid;
	}
	
	public void setEncounterUuid(String encounterUuid) {
		this.encounterUuid = encounterUuid;
	}
	
	@Override
	public String toString() {
		return "ExportProvideAndRegisterParameters ["
				+ "patientUuid = " + patientUuid + ", "
				+ "encounterUuid = " + encounterUuid
				+ "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ExportProvideAndRegisterParameters that = (ExportProvideAndRegisterParameters) o;
		return Objects.equals(patientUuid, that.patientUuid) &&
				Objects.equals(encounterUuid, that.encounterUuid);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(patientUuid, encounterUuid);
	}
}
