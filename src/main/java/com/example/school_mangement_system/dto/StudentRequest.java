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
public class StudentRequest {

    private String name;
    private String username;
    private String password;
    private LocalDate dob;
    private String gender;
    private Long sectionId;
    private String parentContact;
    private String parentName;
    private boolean active;
}
