package com.example.attendance.controller;

import com.example.attendance.model.Subject;
import com.example.attendance.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // 1. Display list of all courses/subjects
    @GetMapping
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects/list"; // Maps to templates/subjects/list.html
    }

    // 2. Show Add Subject form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("subject", new Subject());
        return "subjects/create"; // Maps to templates/subjects/create.html
    }

    // 3. Save subject form data
    @PostMapping
    public String saveSubject(@Valid @ModelAttribute("subject") Subject subject, BindingResult result) {
        if (result.hasErrors()) {
            return "subjects/create";
        }

        // Safety Fallback: Create a placeholder user to avoid SQL foreign key constraints
        if (subject.getTeacher() == null) {
            com.example.attendance.model.User defaultTeacher = new com.example.attendance.model.User();
            defaultTeacher.setId(1L); // Links to your default generated admin ID profile
            subject.setTeacher(defaultTeacher);
        }

        subjectService.saveSubject(subject);
        return "redirect:/admin/subjects";
    }

    // 4. Delete Subject
    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable("id") Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/admin/subjects";
    }
}