package com.airesume.auth.dto;

import com.airesume.user.entity.Role;

public record LoginResponse(
        String token,
        Long userId,
        String name,
        String email,
        Role role
) {}