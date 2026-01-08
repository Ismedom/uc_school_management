package com.example.school_mangement_system.controller;

import com.example.school_mangement_system.dto.TeacherRequest;
import com.example.school_mangement_system.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers/index";
    }

    @GetMapping("/create")
    public String createForm() {
        return "teachers/create";
    }

    @PostMapping
    public String create(@ModelAttribute TeacherRequest request) {
        teacherService.createTeacher(request);
        return "redirect:/teachers";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "teachers/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute TeacherRequest request) {
        teacherService.updateTeacher(id, request);
        return "redirect:/teachers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return "redirect:/teachers";
    }
}
