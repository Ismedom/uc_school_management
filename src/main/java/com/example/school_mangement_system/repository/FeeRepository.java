package com.example.school_mangement_system.repository;

import com.example.school_mangement_system.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findBySchoolClassId(Long classId);
}
