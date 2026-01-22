package com.example.school_mangement_system.controller;

import com.example.school_mangement_system.dto.StudentRequest;
import com.example.school_mangement_system.dto.StudentResponse;
import com.example.school_mangement_system.repository.SectionRepository;
import com.example.school_mangement_system.service.StudentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final SectionRepository sectionRepository;

    @GetMapping
    public String listStudents(Model model) {
        List<StudentResponse> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "students/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("studentRequest", new StudentRequest());
        model.addAttribute("sections", sectionRepository.findAll());
        return "students/create";
    }

    @PostMapping("/create")
    public String createStudent(@ModelAttribute StudentRequest request) {
        studentService.createStudent(request);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        StudentResponse student = studentService.getStudentById(id);
        StudentRequest request = StudentRequest.builder()
            .name(student.getName())
            .username(student.getUsername())
            .dob(student.getDob())
            .gender(student.getGender())
            .sectionId(student.getSectionId())
            .parentContact(student.getParentContact())
            .parentName(student.getParentName())
            .active(student.isActive())
            .build();

        model.addAttribute("studentRequest", request);
        model.addAttribute("studentId", id);
        model.addAttribute("sections", sectionRepository.findAll());
        return "students/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute StudentRequest request) {
        studentService.updateStudent(id, request);
        return "redirect:/students";
    }

    @PostMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}
