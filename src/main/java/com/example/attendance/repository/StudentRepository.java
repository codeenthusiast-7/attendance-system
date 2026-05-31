package com.example.attendance.repository;

import com.example.attendance.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // This custom query powers your Day 3 search feature!
    List<Student> findByNameContainingIgnoreCaseOrCourseContainingIgnoreCase(String name, String course);
}