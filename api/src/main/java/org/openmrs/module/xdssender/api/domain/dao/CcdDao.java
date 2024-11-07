package org.openmrs.module.xdssender.api.domain.dao;

import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.model.RequestDate;

public interface CcdDao {
	
	Ccd saveOrUpdate(Ccd ccd);
	
	Ccd find(Patient patient);

    RequestDate saveOrUpdateRequestDate(RequestDate requestDate) throws APIException ;
	
    RequestDate getLastRequestDate() throws APIException; 
}
