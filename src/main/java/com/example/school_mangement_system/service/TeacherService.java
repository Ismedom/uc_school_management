package com.example.school_mangement_system.service;

import com.example.school_mangement_system.dto.TeacherRequest;
import com.example.school_mangement_system.dto.TeacherResponse;
import com.example.school_mangement_system.entity.Role;
import com.example.school_mangement_system.entity.Teacher;
import com.example.school_mangement_system.entity.User;
import com.example.school_mangement_system.repository.TeacherRepository;
import com.example.school_mangement_system.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));
        return mapToResponse(teacher);
    }

    @Transactional
    public void createTeacher(TeacherRequest request) {
        // Create User for login
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.TEACHER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Create Teacher profile
        Teacher teacher = Teacher.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .qualification(request.getQualification())
            .user(user)
            .build();

        teacherRepository.save(teacher);
    }

    @Transactional
    public void updateTeacher(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));

        teacher.setName(request.getName());
        teacher.setEmail(request.getEmail());
        teacher.setPhone(request.getPhone());
        teacher.setQualification(request.getQualification());

        // Update associated user email/username if needed?
        // For simplicity, keeping username as original email or handling separately.
        if (teacher.getUser() != null) {
            teacher.getUser().setUsername(request.getEmail());
            userRepository.save(teacher.getUser());
        }

        teacherRepository.save(teacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Cascading delete might handle User if configured, but explicit is safer for
        // now
        // JPA CascadeType.ALL on Teacher.user means deleting teacher deletes user?
        // Let's check Teacher entity.
        teacherRepository.delete(teacher);
    }

    private TeacherResponse mapToResponse(Teacher teacher) {
        return TeacherResponse.builder()
            .id(teacher.getId())
            .name(teacher.getName())
            .email(teacher.getEmail())
            .phone(teacher.getPhone())
            .qualification(teacher.getQualification())
            .build();
    }
}
