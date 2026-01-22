package com.example.school_mangement_system.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarksBulkRequest {

    private Long examId;
    private Long subjectId;
    private List<MarksRequest> marks;
}
