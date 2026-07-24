package com.airesume.test;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public String protectedEndpoint(Authentication authentication) {
        return "Authenticated as: " + authentication.getName();
    }

    @GetMapping("/candidate")
    public String candidateEndpoint() {
        return "Candidate access granted";
    }

    @GetMapping("/recruiter")
    public String recruiterEndpoint() {
        return "Recruiter access granted";
    }
}