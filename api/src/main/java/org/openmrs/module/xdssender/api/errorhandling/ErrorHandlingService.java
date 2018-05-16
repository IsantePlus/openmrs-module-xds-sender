package org.openmrs.module.xdssender.api.errorhandling;

public interface ErrorHandlingService {
	
	void handle(String messageBody, String destination, boolean failure, String failureReason);
}
