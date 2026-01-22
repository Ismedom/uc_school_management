package com.example.school_mangement_system.service;

import com.example.school_mangement_system.dto.SubjectRequest;
import com.example.school_mangement_system.dto.SubjectResponse;
import com.example.school_mangement_system.entity.SchoolClass;
import com.example.school_mangement_system.entity.Subject;
import com.example.school_mangement_system.entity.Teacher;
import com.example.school_mangement_system.repository.SchoolClassRepository;
import com.example.school_mangement_system.repository.SubjectRepository;
import com.example.school_mangement_system.repository.TeacherRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final TeacherRepository teacherRepository;

    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Subject not found"));
        return mapToResponse(subject);
    }

    @Transactional
    public void createSubject(SubjectRequest request) {
        SchoolClass schoolClass = schoolClassRepository
            .findById(request.getClassId())
            .orElseThrow(() -> new RuntimeException("Class not found"));

        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository
                .findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        }

        Subject subject = Subject.builder().name(request.getName()).schoolClass(schoolClass).teacher(teacher).build();
        subjectRepository.save(subject);
    }

    @Transactional
    public void updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Subject not found"));

        SchoolClass schoolClass = schoolClassRepository
            .findById(request.getClassId())
            .orElseThrow(() -> new RuntimeException("Class not found"));

        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository
                .findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        }

        subject.setName(request.getName());
        subject.setSchoolClass(schoolClass);
        subject.setTeacher(teacher);
        subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    private SubjectResponse mapToResponse(Subject subject) {
        return SubjectResponse.builder()
            .id(subject.getId())
            .name(subject.getName())
            .classId(subject.getSchoolClass() != null ? subject.getSchoolClass().getId() : null)
            .className(subject.getSchoolClass() != null ? subject.getSchoolClass().getName() : null)
            .teacherId(subject.getTeacher() != null ? subject.getTeacher().getId() : null)
            .teacherName(subject.getTeacher() != null ? subject.getTeacher().getName() : null)
            .build();
    }
}
