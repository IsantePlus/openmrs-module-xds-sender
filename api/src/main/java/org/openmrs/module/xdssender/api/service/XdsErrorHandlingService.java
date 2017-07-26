package org.openmrs.module.xdssender.api.service;

public interface XdsErrorHandlingService {
	
	void handle(String messageBody, String failureReason, String destination, String type, Boolean failure);
}
