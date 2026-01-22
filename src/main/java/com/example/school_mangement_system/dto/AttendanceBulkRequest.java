package com.example.school_mangement_system.dto;

import com.example.school_mangement_system.entity.AttendanceStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceBulkRequest {

    private Long sectionId;
    private Long subjectId;
    private LocalDate date;
    private List<StudentAttendanceRequest> records;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentAttendanceRequest {

        private Long studentId;
        private AttendanceStatus status;
        private String remarks;
    }
}
