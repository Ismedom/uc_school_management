package com.example.school_mangement_system.service;

import com.example.school_mangement_system.dto.MarksBulkRequest;
import com.example.school_mangement_system.dto.MarksRequest;
import com.example.school_mangement_system.dto.MarksResponse;
import com.example.school_mangement_system.entity.Exam;
import com.example.school_mangement_system.entity.Marks;
import com.example.school_mangement_system.entity.Student;
import com.example.school_mangement_system.entity.Subject;
import com.example.school_mangement_system.repository.ExamRepository;
import com.example.school_mangement_system.repository.MarksRepository;
import com.example.school_mangement_system.repository.StudentRepository;
import com.example.school_mangement_system.repository.SubjectRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarksService {

    private final MarksRepository marksRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public List<MarksResponse> getMarksByExamAndSubject(Long examId, Long subjectId) {
        // This is a custom query we might need to add to repository,
        // but for now we filter in memory or update repository.
        // Let's assume we want to view all marks for a specific exam and subject.
        return marksRepository
            .findAll()
            .stream()
            .filter(m -> m.getExam().getId().equals(examId) && m.getSubject().getId().equals(subjectId))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void saveBulkMarks(MarksBulkRequest request) {
        Exam exam = examRepository
            .findById(request.getExamId())
            .orElseThrow(() -> new RuntimeException("Exam not found"));
        Subject subject = subjectRepository
            .findById(request.getSubjectId())
            .orElseThrow(() -> new RuntimeException("Subject not found"));

        for (MarksRequest markReq : request.getMarks()) {
            Student student = studentRepository
                .findById(markReq.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

            // Check if mark already exists for update
            Marks marks = marksRepository
                .findAll()
                .stream()
                .filter(
                    m ->
                        m.getExam().getId().equals(exam.getId()) &&
                        m.getSubject().getId().equals(subject.getId()) &&
                        m.getStudent().getId().equals(student.getId())
                )
                .findFirst()
                .orElse(new Marks());

            marks.setExam(exam);
            marks.setSubject(subject);
            marks.setStudent(student);
            marks.setMarksObtained(markReq.getMarksObtained());
            marks.setMaxMarks(markReq.getMaxMarks());
            marks.setGrade(calculateGrade(markReq.getMarksObtained(), markReq.getMaxMarks()));

            marksRepository.save(marks);
        }
    }

    private String calculateGrade(Double obtained, Double max) {
        if (max == null || max == 0) return "N/A";
        double percentage = (obtained / max) * 100;
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "F";
    }

    private MarksResponse mapToResponse(Marks marks) {
        return MarksResponse.builder()
            .id(marks.getId())
            .examId(marks.getExam().getId())
            .examName(marks.getExam().getName())
            .studentId(marks.getStudent().getId())
            .studentName(marks.getStudent().getName())
            .subjectId(marks.getSubject().getId())
            .subjectName(marks.getSubject().getName())
            .marksObtained(marks.getMarksObtained())
            .maxMarks(marks.getMaxMarks())
            .grade(marks.getGrade())
            .build();
    }
}
