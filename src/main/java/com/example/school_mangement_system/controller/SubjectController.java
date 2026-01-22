package com.example.school_mangement_system.controller;

import com.example.school_mangement_system.dto.SubjectRequest;
import com.example.school_mangement_system.service.SchoolClassService;
import com.example.school_mangement_system.service.SubjectService;
import com.example.school_mangement_system.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    private final SchoolClassService schoolClassService;
    private final TeacherService teacherService;

    @GetMapping
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("subjectRequest", new SubjectRequest());
        model.addAttribute("classes", schoolClassService.getAllClasses());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "subjects/create";
    }

    @PostMapping("/create")
    public String createSubject(@ModelAttribute SubjectRequest request) {
        subjectService.createSubject(request);
        return "redirect:/subjects";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("subjectRequest", subjectService.getSubjectById(id));
        model.addAttribute("subjectId", id);
        model.addAttribute("classes", schoolClassService.getAllClasses());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "subjects/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateSubject(@PathVariable Long id, @ModelAttribute SubjectRequest request) {
        subjectService.updateSubject(id, request);
        return "redirect:/subjects";
    }

    @PostMapping("/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/subjects";
    }
}
