package com.example.school_mangement_system.repository;

import com.example.school_mangement_system.entity.Attendance;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByDate(LocalDate date);

    java.util.Optional<Attendance> findByStudentIdAndDate(Long studentId, LocalDate date);
}
