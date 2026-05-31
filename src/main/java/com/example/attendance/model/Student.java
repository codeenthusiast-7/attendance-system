package com.example.attendance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "students")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Course is required")
    private String course;

    // Changed from section to kid
    @NotBlank(message = "KID is required")
    @Column(name = "kid", unique = true, nullable = false)
    private String kid;

    @Email(message = "Provide a valid email address")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    // CRITICAL FIX: Automatically cascades a deletion from the student record down to their historical attendance matrix rows
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendanceRecords;
}