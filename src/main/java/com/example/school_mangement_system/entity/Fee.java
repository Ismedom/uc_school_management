package com.example.school_mangement_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fees")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Column(nullable = false)
    private String title; // "Tuition Fee", "Lab Fee"

    @Column(nullable = false)
    private BigDecimal amount;

    // Is this a fee *structure* defined for the class, or a fee assigned to a
    // student?
    // "Fee structure per class"
    // "Record payments" -> "Payment status: Paid/Partial/Due".
    // This implies we generate a "StudentFee" record when it's due? Or we just
    // calculate balance?
    // "Fee due report".
    // Let's create `FeeStructure` (assigned to Class) and `StudentFee` (assigned to
    // Student)?
    // Or just "Fee" linked to Student directly?
    // Requirement says "Fee structure per class".
    // So this entity "Fee" can represent the structure.

    // We need a way to track STUDENT payments against this fee.
    // Let's call this entity `FeeStructure` or just `Fee`.
    // And `Payment` links to `Student` and `Fee`.

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
