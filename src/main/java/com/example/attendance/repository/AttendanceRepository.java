package com.example.attendance.repository;

import com.example.attendance.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Custom database search query to see if attendance was already marked for a specific course on a specific day
    List<Attendance> findBySubjectIdAndDate(Long subjectId, LocalDate date);
}