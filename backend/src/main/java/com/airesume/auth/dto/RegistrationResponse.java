package com.airesume.auth.dto;

import com.airesume.user.entity.Role;

public record RegistrationResponse(
        Long id,
        String name,
        String email,
        Role role
) {}