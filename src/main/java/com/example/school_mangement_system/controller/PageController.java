package com.example.school_mangement_system.controller;

import com.example.school_mangement_system.entity.Student;
import com.example.school_mangement_system.entity.User;
import com.example.school_mangement_system.repository.SchoolClassRepository;
import com.example.school_mangement_system.repository.StudentRepository;
import com.example.school_mangement_system.repository.TeacherRepository;
import com.example.school_mangement_system.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String root() {
        return "auth/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", studentRepository.count());
        model.addAttribute("totalTeachers", teacherRepository.count());
        model.addAttribute("totalClasses", schoolClassRepository.count());
        return "dashboard/index";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        // Find student by user
        Student student = studentRepository
            .findAll()
            .stream()
            .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Student not found"));

        model.addAttribute("student", student);
        // For now, we'll show empty attendance - you can implement attendance fetching later
        model.addAttribute("recentAttendance", List.of());

        return "student/dashboard";
    }
}
