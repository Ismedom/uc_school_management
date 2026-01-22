package com.example.school_mangement_system.repository;

import com.example.school_mangement_system.entity.Fee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findBySchoolClassId(Long classId);
}
