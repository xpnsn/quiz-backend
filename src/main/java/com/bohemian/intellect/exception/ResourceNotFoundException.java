package com.bohemian.intellect.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String id, String type) {
        super(type+" not found with id : "+id);
    }
}
