package org.openmrs.module.xdssender.api.errorhandling;

import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

public class RetrieveAndSaveCcdParameters implements Serializable {

	private String patientUuid;

	public RetrieveAndSaveCcdParameters() {
	}

	public RetrieveAndSaveCcdParameters(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	public String getPatientUuid() {
		return patientUuid;
	}
	
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	@Override
	public String toString() {
		return "RetrieveAndSaveCcdParameters [patientUuid = " + patientUuid + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RetrieveAndSaveCcdParameters that = (RetrieveAndSaveCcdParameters) o;
		return StringUtils.equals(patientUuid, that.patientUuid);
	}
	
	@Override
	public int hashCode() {
		return patientUuid.hashCode();
	}
}
