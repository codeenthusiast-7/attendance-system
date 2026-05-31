package com.example.attendance.service;

import com.example.attendance.model.Attendance;
import com.example.attendance.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    // Constructor Injection to link our Repository Layer
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    // 1. Batch save optimization to commit the entire classroom sheet at once
    public void saveAttendanceRecords(List<Attendance> attendanceList) {
        attendanceRepository.saveAll(attendanceList);
    }

    // 2. Lookup query to check if a course already has attendance saved for today
    public List<Attendance> getAttendanceBySubjectAndDate(Long subjectId, LocalDate date) {
        return attendanceRepository.findBySubjectIdAndDate(subjectId, date);
    }

    // 3. CRITICAL FIX: Added to fetch the complete tracking history ledger for aggregate percentage math
    public List<Attendance> getAllAttendanceRecords() {
        return attendanceRepository.findAll();
    }
}