package org.openmrs.module.xdssender.api.domain;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Table(name = "xdssender_ccd")
@NamedQueries({ @NamedQuery(name = "findCcdByPatient", query = "from Ccd c where c.patient = :patient") })
public class Ccd extends BaseOpenmrsData {
	
	@Id
	@Column(name = "xdssender_ccd_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;
	
	@Column
	private String document;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public String getDocument() {
		return document;
	}
	
	public void setDocument(String document) {
		this.document = document;
	}
	
	@Transient
	public Date getDownloadDate() {
		return getDateChanged() == null ? getDateCreated() : getDateChanged();
	}
}
