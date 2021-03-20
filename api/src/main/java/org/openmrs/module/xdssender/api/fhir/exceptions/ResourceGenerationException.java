package org.openmrs.module.xdssender.api.fhir.exceptions;


public class ResourceGenerationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ResourceGenerationException(String errorMessage) {
		super(errorMessage);
	}
	
}
