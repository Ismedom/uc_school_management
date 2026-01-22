package com.example.school_mangement_system.repository;

import com.example.school_mangement_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    java.util.List<Student> findBySectionId(Long sectionId);
}
