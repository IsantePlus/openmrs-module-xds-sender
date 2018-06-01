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

        int code = response.getStatusLine().getStatusCode();
        if (!is2xxSuccessful(code)) {
            exception = new Exception(String.format("CCD import failure. Http response: "
                    + "code %d, message \n %s", code, response));
        } else {
            exception = null;
        }
    }

    public boolean inError() {
        return response == null || exception != null;
    }

    public Exception getException() {
        return exception;
    }

    public HttpResponse getResponse() {
        return response;
    }

    private boolean is2xxSuccessful(int code) {
        return code >= 200 && code < 300;
    }
}
