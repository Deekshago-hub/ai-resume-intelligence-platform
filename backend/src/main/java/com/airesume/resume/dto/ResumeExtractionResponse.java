package com.airesume.resume.dto;

public record ResumeExtractionResponse(
        String fileName,
        String extractedText,
        int characterCount
) {}
