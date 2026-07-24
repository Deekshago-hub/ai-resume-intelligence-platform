package com.airesume.resume.service;

import com.airesume.resume.dto.ResumeExtractionResponse;
import com.airesume.resume.exception.ResumeApiException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeService {

    static final long MAX_FILE_SIZE_BYTES = 5L * 1024L * 1024L;
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    public ResumeExtractionResponse extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResumeApiException(
                    HttpStatus.BAD_REQUEST,
                    "Resume file must not be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResumeApiException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Resume file size must not exceed 5 MB"
            );
        }

        if (!isPdfFile(file)) {
            throw new ResumeApiException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF files are supported"
            );
        }

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String extractedText = textStripper.getText(document).trim();

            if (extractedText.isBlank()) {
                throw new ResumeApiException(
                        HttpStatus.BAD_REQUEST,
                        "No meaningful text could be extracted from this PDF"
                );
            }

            return new ResumeExtractionResponse(
                    file.getOriginalFilename(),
                    extractedText,
                    extractedText.length()
            );
        } catch (IOException exception) {
            throw new ResumeApiException(
                    HttpStatus.BAD_REQUEST,
                    "Unable to read the PDF file"
            );
        }
    }

    private boolean isPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        boolean contentTypeIsPdf = PDF_CONTENT_TYPE.equalsIgnoreCase(contentType);
        boolean extensionIsPdf = fileName != null && fileName.toLowerCase().endsWith(".pdf");

        return contentTypeIsPdf || extensionIsPdf;
    }
}
