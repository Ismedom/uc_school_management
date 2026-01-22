package com.example.school_mangement_system.dto;

import com.example.school_mangement_system.entity.AttendanceStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private LocalDate date;
    private AttendanceStatus status;
    private String remarks;
}
