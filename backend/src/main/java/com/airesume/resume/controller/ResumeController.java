package com.airesume.resume.controller;

import com.airesume.resume.dto.ResumeExtractionResponse;
import com.airesume.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(
            value = "/extract",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResumeExtractionResponse extractResumeText(
            @RequestParam("file") MultipartFile file) {

        return resumeService.extractText(file);
    }
}
