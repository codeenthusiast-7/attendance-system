package com.example.attendance.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship: Link to a specific Student record
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Relationship: Link to a specific Subject record
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String status; // Will store values: "Present", "Absent", or "Late"
}