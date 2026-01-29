package com.example.school_mangement_system.service;

import com.example.school_mangement_system.dto.SchoolClassRequest;
import com.example.school_mangement_system.dto.SchoolClassResponse;
import com.example.school_mangement_system.entity.SchoolClass;
import com.example.school_mangement_system.entity.Section;
import com.example.school_mangement_system.repository.SchoolClassRepository;
import com.example.school_mangement_system.repository.SectionRepository;
import com.example.school_mangement_system.repository.StudentRepository;
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
    private final StudentRepository studentRepository;

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

        if (request.getSectionNames() != null) {
            var existingSections = sectionRepository.findBySchoolClassId(id);
            var existingSectionNames = existingSections.stream().map(Section::getName).toList();

            for (var section : existingSections) {
                if (!request.getSectionNames().contains(section.getName())) {
                    long studentCount = studentRepository.countBySectionId(section.getId());
                    if (studentCount > 0) {
                        throw new RuntimeException(
                            "Cannot delete section '" +
                                section.getName() +
                                "' because it has " +
                                studentCount +
                                " student(s) assigned. Please reassign students first."
                        );
                    }
                    sectionRepository.delete(section);
                }
            }

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
