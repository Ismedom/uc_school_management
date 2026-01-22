package com.example.school_mangement_system.service;

import com.example.school_mangement_system.dto.SchoolClassRequest;
import com.example.school_mangement_system.dto.SchoolClassResponse;
import com.example.school_mangement_system.entity.SchoolClass;
import com.example.school_mangement_system.entity.Section;
import com.example.school_mangement_system.repository.SchoolClassRepository;
import com.example.school_mangement_system.repository.SectionRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final SectionRepository sectionRepository;

    public List<SchoolClassResponse> getAllClasses() {
        return schoolClassRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public SchoolClassResponse getClassById(Long id) {
        SchoolClass schoolClass = schoolClassRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        return mapToResponse(schoolClass);
    }

    @Transactional
    public void createClass(SchoolClassRequest request) {
        SchoolClass schoolClass = SchoolClass.builder().name(request.getName()).build();
        schoolClass = schoolClassRepository.save(schoolClass);

        // Create sections if provided
        if (request.getSectionNames() != null && !request.getSectionNames().isEmpty()) {
            for (String sectionName : request.getSectionNames()) {
                if (sectionName != null && !sectionName.trim().isEmpty()) {
                    Section section = Section.builder().name(sectionName.trim()).schoolClass(schoolClass).build();
                    sectionRepository.save(section);
                }
            }
        }
    }

    @Transactional
    public void updateClass(Long id, SchoolClassRequest request) {
        SchoolClass schoolClass = schoolClassRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        schoolClass.setName(request.getName());
        schoolClass = schoolClassRepository.save(schoolClass);

        // Handle section updates
        if (request.getSectionNames() != null) {
            // Get existing sections
            var existingSections = sectionRepository.findBySchoolClassId(id);
            var existingSectionNames = existingSections.stream().map(Section::getName).toList();

            // Remove sections that are no longer in the request
            for (var section : existingSections) {
                if (!request.getSectionNames().contains(section.getName())) {
                    sectionRepository.delete(section);
                }
            }

            // Add new sections
            for (String sectionName : request.getSectionNames()) {
                if (
                    sectionName != null &&
                    !sectionName.trim().isEmpty() &&
                    !existingSectionNames.contains(sectionName.trim())
                ) {
                    Section section = Section.builder().name(sectionName.trim()).schoolClass(schoolClass).build();
                    sectionRepository.save(section);
                }
            }
        }
    }

    @Transactional
    public void deleteClass(Long id) {
        schoolClassRepository.deleteById(id);
    }

    private SchoolClassResponse mapToResponse(SchoolClass schoolClass) {
        return SchoolClassResponse.builder()
            .id(schoolClass.getId())
            .name(schoolClass.getName())
            .createdAt(schoolClass.getCreatedAt())
            .updatedAt(schoolClass.getUpdatedAt())
            .build();
    }
}
