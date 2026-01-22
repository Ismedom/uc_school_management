package com.example.school_mangement_system.controller;

import com.example.school_mangement_system.dto.*;
import com.example.school_mangement_system.entity.Student;
import com.example.school_mangement_system.repository.SchoolClassRepository;
import com.example.school_mangement_system.repository.StudentRepository;
import com.example.school_mangement_system.repository.SubjectRepository;
import com.example.school_mangement_system.service.ExamService;
import com.example.school_mangement_system.service.MarksService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final MarksService marksService;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    @GetMapping
    public String listExams(Model model) {
        model.addAttribute("exams", examService.getAllExams());
        return "exams/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("examRequest", new ExamRequest());
        model.addAttribute("classes", schoolClassRepository.findAll());
        return "exams/create";
    }

    @PostMapping("/create")
    public String createExam(@ModelAttribute ExamRequest request) {
        examService.createExam(request);
        return "redirect:/exams";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ExamResponse exam = examService.getExamById(id);
        ExamRequest request = ExamRequest.builder()
            .name(exam.getName())
            .startDate(exam.getStartDate())
            .endDate(exam.getEndDate())
            .classId(exam.getClassId())
            .build();
        model.addAttribute("examRequest", request);
        model.addAttribute("examId", id);
        model.addAttribute("classes", schoolClassRepository.findAll());
        return "exams/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateExam(@PathVariable Long id, @ModelAttribute ExamRequest request) {
        examService.updateExam(id, request);
        return "redirect:/exams";
    }

    @PostMapping("/delete/{id}")
    public String deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return "redirect:/exams";
    }

    // Grading routes
    @GetMapping("/{examId}/grades")
    public String showGradeSelection(
        @PathVariable Long examId,
        @RequestParam(required = false) Long subjectId,
        Model model
    ) {
        ExamResponse exam = examService.getExamById(examId);
        model.addAttribute("exam", exam);

        if (exam.getClassId() != null) {
            model.addAttribute("subjects", subjectRepository.findBySchoolClassId(exam.getClassId()));
        } else {
            model.addAttribute("subjects", subjectRepository.findAll());
        }

        if (subjectId != null) {
            model.addAttribute("selectedSubjectId", subjectId);
            model.addAttribute("subject", subjectRepository.findById(subjectId).orElseThrow());

            List<MarksResponse> existingMarks = marksService.getMarksByExamAndSubject(examId, subjectId);

            // Build the bulk request
            MarksBulkRequest bulkRequest = new MarksBulkRequest();
            bulkRequest.setExamId(examId);
            bulkRequest.setSubjectId(subjectId);

            List<Student> students;
            if (exam.getClassId() != null) {
                students = studentRepository
                    .findAll()
                    .stream()
                    .filter(
                        s ->
                            s.getSection() != null &&
                            s.getSection().getSchoolClass() != null &&
                            s.getSection().getSchoolClass().getId().equals(exam.getClassId())
                    )
                    .toList();
            } else {
                students = studentRepository.findAll();
            }

            List<MarksRequest> marksRequests = students
                .stream()
                .map(student -> {
                    MarksResponse existing = existingMarks
                        .stream()
                        .filter(m -> m.getStudentId().equals(student.getId()))
                        .findFirst()
                        .orElse(null);

                    return MarksRequest.builder()
                        .studentId(student.getId())
                        .marksObtained(existing != null ? existing.getMarksObtained() : 0.0)
                        .maxMarks(existing != null ? existing.getMaxMarks() : 100.0)
                        .build();
                })
                .collect(java.util.stream.Collectors.toList());

            bulkRequest.setMarks(marksRequests);
            model.addAttribute("bulkRequest", bulkRequest);
            model.addAttribute("studentsList", students); // for displaying names
        }

        return "exams/grades";
    }

    @PostMapping("/{examId}/grades")
    public String saveGrades(@PathVariable Long examId, @ModelAttribute MarksBulkRequest request) {
        request.setExamId(examId);
        marksService.saveBulkMarks(request);
        return "redirect:/exams/" + examId + "/grades?subjectId=" + request.getSubjectId();
    }
}
