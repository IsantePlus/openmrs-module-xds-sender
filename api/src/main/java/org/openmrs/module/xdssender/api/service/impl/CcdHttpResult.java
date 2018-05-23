package org.openmrs.module.xdssender.api.service.impl;

import org.apache.http.HttpResponse;

public class CcdHttpResult {

    private final Exception exception;
    private final HttpResponse response;

    public CcdHttpResult(Exception exception) {
        this.exception = exception;
        this.response = null;
    }

    public CcdHttpResult(HttpResponse response) {
        this.response = response;
        this.exception = null;
    }

    public boolean inError() {
        return response == null;
    }

    public Exception getException() {
        return exception;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
