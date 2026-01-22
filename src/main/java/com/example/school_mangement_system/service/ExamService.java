package com.example.school_mangement_system.service;

import com.example.school_mangement_system.dto.ExamRequest;
import com.example.school_mangement_system.dto.ExamResponse;
import com.example.school_mangement_system.entity.Exam;
import com.example.school_mangement_system.entity.SchoolClass;
import com.example.school_mangement_system.repository.ExamRepository;
import com.example.school_mangement_system.repository.SchoolClassRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SchoolClassRepository schoolClassRepository;

    public List<ExamResponse> getAllExams() {
        return examRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id).orElseThrow(() -> new RuntimeException("Exam not found"));
        return mapToResponse(exam);
    }

    @Transactional
    public void createExam(ExamRequest request) {
        SchoolClass schoolClass = null;
        if (request.getClassId() != null) {
            schoolClass = schoolClassRepository
                .findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        }

        Exam exam = Exam.builder()
            .name(request.getName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .schoolClass(schoolClass)
            .build();
        examRepository.save(exam);
    }

    @Transactional
    public void updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.findById(id).orElseThrow(() -> new RuntimeException("Exam not found"));

        SchoolClass schoolClass = null;
        if (request.getClassId() != null) {
            schoolClass = schoolClassRepository
                .findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));
        }

        exam.setName(request.getName());
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setSchoolClass(schoolClass);
        examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

    private ExamResponse mapToResponse(Exam exam) {
        return ExamResponse.builder()
            .id(exam.getId())
            .name(exam.getName())
            .startDate(exam.getStartDate())
            .endDate(exam.getEndDate())
            .classId(exam.getSchoolClass() != null ? exam.getSchoolClass().getId() : null)
            .className(exam.getSchoolClass() != null ? exam.getSchoolClass().getName() : null)
            .build();
    }
}
