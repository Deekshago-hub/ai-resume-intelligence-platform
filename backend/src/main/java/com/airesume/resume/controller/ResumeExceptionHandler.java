package com.airesume.resume.controller;

import com.airesume.resume.dto.ResumeErrorResponse;
import com.airesume.resume.exception.ResumeApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ResumeExceptionHandler {

    @ExceptionHandler(ResumeApiException.class)
    public ResponseEntity<ResumeErrorResponse> handleResumeApiException(
            ResumeApiException exception) {

        return ResponseEntity
                .status(exception.getStatus())
                .body(new ResumeErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResumeErrorResponse> handleMaxUploadSizeExceeded() {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ResumeErrorResponse("Resume file size must not exceed 5 MB"));
    }
}
