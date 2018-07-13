package org.openmrs.module.xdssender.api.model;

public class DocumentData {

    private DocumentInfo documentInfo;

    private byte[] documentContent;

    public DocumentData(DocumentInfo documentInfo, byte[] documentContent) {
        this.documentInfo = documentInfo;
        this.documentContent = documentContent;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public byte[] getDocumentContent() {
        return documentContent;
    }
}
