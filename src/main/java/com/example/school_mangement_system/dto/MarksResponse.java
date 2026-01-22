package com.example.school_mangement_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarksResponse {

    private Long id;
    private Long examId;
    private String examName;
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private Double marksObtained;
    private Double maxMarks;
    private String grade;
}
