package com.example.school_mangement_system.service;

import com.example.school_mangement_system.dto.AttendanceBulkRequest;
import com.example.school_mangement_system.dto.AttendanceResponse;
import com.example.school_mangement_system.entity.Attendance;
import com.example.school_mangement_system.entity.Student;
import com.example.school_mangement_system.repository.AttendanceRepository;
import com.example.school_mangement_system.repository.StudentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public List<AttendanceResponse> getAttendanceBySectionAndDate(Long sectionId, LocalDate date) {
        List<Student> students = studentRepository.findBySectionId(sectionId);
        return students
            .stream()
            .map(student -> {
                Attendance attendance = attendanceRepository.findByStudentIdAndDate(student.getId(), date).orElse(null);
                return AttendanceResponse.builder()
                    .studentId(student.getId())
                    .studentName(student.getName())
                    .date(date)
                    .status(attendance != null ? attendance.getStatus() : null)
                    .remarks(attendance != null ? attendance.getRemarks() : null)
                    .id(attendance != null ? attendance.getId() : null)
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void saveBulkAttendance(AttendanceBulkRequest request) {
        for (AttendanceBulkRequest.StudentAttendanceRequest record : request.getRecords()) {
            Attendance attendance = attendanceRepository
                .findByStudentIdAndDate(record.getStudentId(), request.getDate())
                .orElse(new Attendance());

            if (attendance.getId() == null) {
                Student student = studentRepository
                    .findById(record.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
                attendance.setStudent(student);
                attendance.setDate(request.getDate());
            }

            attendance.setStatus(record.getStatus());
            attendance.setRemarks(record.getRemarks());
            attendanceRepository.save(attendance);
        }
    }

    public List<AttendanceResponse> getStudentAttendanceHistory(Long studentId) {
        return attendanceRepository
            .findByStudentId(studentId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
            .id(attendance.getId())
            .studentId(attendance.getStudent().getId())
            .studentName(attendance.getStudent().getName())
            .date(attendance.getDate())
            .status(attendance.getStatus())
            .remarks(attendance.getRemarks())
            .build();
    }
}
