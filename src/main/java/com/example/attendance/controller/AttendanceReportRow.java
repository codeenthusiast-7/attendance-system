package com.example.attendance.controller;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AttendanceReportRow {
    private Long studentId;
    private String studentName;
    private String studentKid;
    private long presentCount;
    private long absentCount;
    private double percentage;
}