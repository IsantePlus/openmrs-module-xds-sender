package org.openmrs.module.xdssender.api.domain.dao.impl;

import org.hibernate.Query;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.domain.dao.CcdDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CcdDaoImpl implements CcdDao {
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	@Override
	public Ccd saveOrUpdate(Ccd ccd) {
		Ccd existing = find(ccd.getPatient());
		
		if (existing == null) {
			sessionFactory.getCurrentSession().save(ccd);
			return ccd;
		} else {
			existing.setDocument(ccd.getDocument());
			sessionFactory.getCurrentSession().update(existing);
			return existing;
		}
	}
	
	@Override
	public Ccd find(Patient patient) {
		Query query = sessionFactory.getCurrentSession().getNamedQuery("findCcdByPatient");
		query.setParameter("patient", patient);
		return (Ccd) query.uniqueResult();
	}
}
