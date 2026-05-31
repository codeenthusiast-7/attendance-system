package com.example.attendance.controller;

import com.example.attendance.service.StudentService;
import com.example.attendance.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class HomeController {

    private final StudentService studentService;
    private final SubjectService subjectService;

    // Injecting dependencies to execute table count aggregations
    public HomeController(StudentService studentService, SubjectService subjectService) {
        this.studentService = studentService;
        this.subjectService = subjectService;
    }

    // Map the root URL index path (http://localhost:8080/) to our fresh dashboard metrics screen
    @GetMapping("/")
    public String showMainDashboard(Model model) {

        // Count database lists dynamically
        long totalStudents = studentService.getAllStudents().size();
        long totalSubjects = subjectService.getAllSubjects().size();

        model.addAttribute("studentCount", totalStudents);
        model.addAttribute("subjectCount", totalSubjects);
        model.addAttribute("currentDate", LocalDate.now());

        return "index"; // Maps cleanly to templates/index.html
    }
}