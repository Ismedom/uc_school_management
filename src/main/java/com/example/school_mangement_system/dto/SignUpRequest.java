package com.example.school_mangement_system.dto;

import com.example.school_mangement_system.entity.Role;

public record SignUpRequest(
        String username,
        String password,
        Role role) {
}
