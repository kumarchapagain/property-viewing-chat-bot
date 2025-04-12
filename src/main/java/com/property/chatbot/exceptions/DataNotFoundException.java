package com.property.chatbot.exceptions;

public class DataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    String field;
    String fieldName;
    Long fieldId;

    public DataNotFoundException(String fieldName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", fieldName, field, fieldId));
        this.fieldName = fieldName;
        this.field = field;
        this.fieldId = fieldId;
    }
}
