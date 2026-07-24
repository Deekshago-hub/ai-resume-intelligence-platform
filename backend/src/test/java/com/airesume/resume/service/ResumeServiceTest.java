package com.airesume.resume.service;

import com.airesume.resume.dto.ResumeExtractionResponse;
import com.airesume.resume.exception.ResumeApiException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ResumeServiceTest {

    private final ResumeService resumeService = new ResumeService();

    @Test
    void extractTextSuccessfullyFromPdf() throws IOException {
        byte[] pdfBytes = createPdfWithText("Java Spring Developer");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                pdfBytes
        );

        ResumeExtractionResponse response = resumeService.extractText(file);

        assertEquals("resume.pdf", response.fileName());
        assertTrue(response.extractedText().contains("Java Spring Developer"));
        assertEquals(
                response.extractedText().length(),
                response.characterCount()
        );
    }

    @Test
    void rejectsEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                new byte[0]
        );

        ResumeApiException exception = assertThrows(
                ResumeApiException.class,
                () -> resumeService.extractText(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void rejectsNonPdfFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                "not a pdf".getBytes()
        );

        ResumeApiException exception = assertThrows(
                ResumeApiException.class,
                () -> resumeService.extractText(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void rejectsOversizedFile() {
        byte[] oversizedContent = new byte[(int) ResumeService.MAX_FILE_SIZE_BYTES + 1];

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                oversizedContent
        );

        ResumeApiException exception = assertThrows(
                ResumeApiException.class,
                () -> resumeService.extractText(file)
        );

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, exception.getStatus());
    }

    @Test
    void rejectsPdfWithoutMeaningfulText() throws IOException {
        byte[] blankPdf = createPdfWithText("   ");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                blankPdf
        );

        ResumeApiException exception = assertThrows(
                ResumeApiException.class,
                () -> resumeService.extractText(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    private byte[] createPdfWithText(String text) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        12
                );
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText(text);
                contentStream.endText();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
