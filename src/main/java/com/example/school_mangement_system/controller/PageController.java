package com.example.school_mangement_system.controller;

import com.example.school_mangement_system.repository.SchoolClassRepository;
import com.example.school_mangement_system.repository.StudentRepository;
import com.example.school_mangement_system.repository.TeacherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolClassRepository schoolClassRepository;

    public PageController(
        StudentRepository studentRepository,
        TeacherRepository teacherRepository,
        SchoolClassRepository schoolClassRepository
    ) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.schoolClassRepository = schoolClassRepository;
    }

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
}
