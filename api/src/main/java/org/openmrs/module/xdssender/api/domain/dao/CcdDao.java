package org.openmrs.module.xdssender.api.domain.dao;

import org.openmrs.Patient;
import org.openmrs.module.xdssender.api.domain.Ccd;

public interface CcdDao {
	
	Ccd saveOrUpdate(Ccd ccd);
	
	Ccd find(Patient patient);
}
