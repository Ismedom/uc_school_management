package com.example.school_mangement_system.controller;

import com.example.school_mangement_system.dto.SchoolClassRequest;
import com.example.school_mangement_system.entity.Section;
import com.example.school_mangement_system.repository.SectionRepository;
import com.example.school_mangement_system.service.SchoolClassService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService schoolClassService;
    private final SectionRepository sectionRepository;

    @GetMapping
    public String listClasses(Model model) {
        model.addAttribute("classes", schoolClassService.getAllClasses());
        return "classes/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("classRequest", new SchoolClassRequest());
        return "classes/create";
    }

    @PostMapping("/create")
    public String createClass(@ModelAttribute SchoolClassRequest request) {
        schoolClassService.createClass(request);
        return "redirect:/classes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var classResponse = schoolClassService.getClassById(id);
        List<Section> sections = sectionRepository.findBySchoolClassId(id);

        // Create request object with existing data
        var classRequest = SchoolClassRequest.builder()
            .name(classResponse.getName())
            .sectionNames(sections.stream().map(Section::getName).toList())
            .build();

        model.addAttribute("classRequest", classRequest);
        model.addAttribute("classId", id);
        model.addAttribute("existingSections", sections);
        return "classes/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateClass(@PathVariable Long id, @ModelAttribute SchoolClassRequest request) {
        try {
            schoolClassService.updateClass(id, request);
            return "redirect:/classes";
        } catch (RuntimeException e) {
            // Redirect back with error message
            return (
                "redirect:/classes/edit/" +
                id +
                "?error=" +
                java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8)
            );
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteClass(@PathVariable Long id) {
        schoolClassService.deleteClass(id);
        return "redirect:/classes";
    }
}
