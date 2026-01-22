package com.example.school_mangement_system.config;

import com.example.school_mangement_system.dto.SignUpRequest;
import com.example.school_mangement_system.entity.Role;
import com.example.school_mangement_system.entity.SchoolClass;
import com.example.school_mangement_system.entity.Section;
import com.example.school_mangement_system.repository.SchoolClassRepository;
import com.example.school_mangement_system.repository.SectionRepository;
import com.example.school_mangement_system.repository.UserRepository;
import com.example.school_mangement_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final SchoolClassRepository classRepository;
    private final SectionRepository sectionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("superadmin")) {
            userService.registerUser(new SignUpRequest("superadmin", "superadmin123", Role.SUPER_ADMIN));
            System.out.println("Seeded Super Admin user");
        }

        if (!userRepository.existsByUsername("admin")) {
            userService.registerUser(new SignUpRequest("admin", "admin123", Role.ADMIN));
            System.out.println("Seeded Admin user");
        }
    }
}
