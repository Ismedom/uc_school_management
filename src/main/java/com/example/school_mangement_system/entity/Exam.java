package com.example.school_mangement_system.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Midterm 2025", "Final"

    private LocalDate startDate;
    private LocalDate endDate;

    // Could link to Class if exams are class-specific, or be school-wide.
    // "Create exams (Midterm, Final)" usually implies a term or event.
    // However, marks are entered "per subject".
    // So "Exam" is the event. "ExamSubject" could be the scheduled paper?
    // Let's keep Exam as the event container.

    // Or maybe Exam is linked to a Class? "Grade 1 Midterm".

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass; // Optional: if null, applies to all? For now, let's say exams are per class
    // structure to align with subjects.
}
