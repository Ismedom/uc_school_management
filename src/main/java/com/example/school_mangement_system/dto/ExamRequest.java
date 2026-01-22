package com.example.school_mangement_system.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamRequest {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long classId;
}
