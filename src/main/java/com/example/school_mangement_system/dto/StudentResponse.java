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
public class StudentResponse {

    private Long id;
    private String name;
    private String username;
    private LocalDate dob;
    private String gender;
    private Long sectionId;
    private String sectionName;
    private String className;
    private String parentContact;
    private String parentName;
    private boolean active;
}
