package com.airesume.resume.exception;

import org.springframework.http.HttpStatus;

public class ResumeApiException extends RuntimeException {

    private final HttpStatus status;

    public ResumeApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
