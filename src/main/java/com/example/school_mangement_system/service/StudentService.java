package com.example.school_mangement_system.service;

import com.example.school_mangement_system.dto.StudentRequest;
import com.example.school_mangement_system.dto.StudentResponse;
import com.example.school_mangement_system.entity.Role;
import com.example.school_mangement_system.entity.Section;
import com.example.school_mangement_system.entity.Student;
import com.example.school_mangement_system.entity.User;
import com.example.school_mangement_system.repository.SectionRepository;
import com.example.school_mangement_system.repository.StudentRepository;
import com.example.school_mangement_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final PasswordEncoder passwordEncoder;

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return mapToResponse(student);
    }

    @Transactional
    public void createStudent(StudentRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setActive(true);
        user = userRepository.save(user);

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));

        Student student = Student.builder()
                .user(user)
                .name(request.getName())
                .dob(request.getDob())
                .gender(request.getGender())
                .section(section)
                .parentContact(request.getParentContact())
                .parentName(request.getParentName())
                .active(true)
                .build();

        studentRepository.save(student);
    }

    @Transactional
    public void updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setName(request.getName());
        student.setDob(request.getDob());
        student.setGender(request.getGender());
        student.setParentContact(request.getParentContact());
        student.setParentName(request.getParentName());
        student.setActive(request.isActive());

        if (request.getSectionId() != null) {
            Section section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new RuntimeException("Section not found"));
            student.setSection(section);
        }

        // Update user if needed (e.g., active status)
        User user = student.getUser();
        user.setActive(request.isActive());
        userRepository.save(user);

        studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        studentRepository.delete(student);
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .username(student.getUser().getUsername())
                .dob(student.getDob())
                .gender(student.getGender())
                .sectionId(student.getSection() != null ? student.getSection().getId() : null)
                .sectionName(student.getSection() != null ? student.getSection().getName() : null)
                .className(student.getSection() != null && student.getSection().getSchoolClass() != null
                        ? student.getSection().getSchoolClass().getName()
                        : null)
                .parentContact(student.getParentContact())
                .parentName(student.getParentName())
                .active(student.isActive())
                .build();
    }
}
