package com.airesume.resume.controller;

import com.airesume.config.SecurityConfig;
import com.airesume.resume.dto.ResumeExtractionResponse;
import com.airesume.resume.service.ResumeService;
import com.airesume.security.CustomUserDetailsService;
import com.airesume.security.JwtAuthenticationFilter;
import com.airesume.security.JwtService;
import com.airesume.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ResumeController.class)
@Import({
        SecurityConfig.class,
        ResumeExceptionHandler.class,
        JwtAuthenticationFilter.class,
        CustomUserDetailsService.class
})
class ResumeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResumeService resumeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "pdf".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/resumes/extract").file(file)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "RECRUITER")
    void rejectsRecruiterUser() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "pdf".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/resumes/extract").file(file)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void allowsCandidateUserAndReturnsExtractionResponse() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "pdf".getBytes()
        );

        when(resumeService.extractText(any()))
                .thenReturn(new ResumeExtractionResponse(
                        "resume.pdf",
                        "Extracted resume text",
                        21
                ));

        mockMvc.perform(
                        multipart("/api/resumes/extract").file(file)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("resume.pdf"))
                .andExpect(jsonPath("$.extractedText").value("Extracted resume text"))
                .andExpect(jsonPath("$.characterCount").value(21));
    }
}
