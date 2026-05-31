package com.example.attendance.controller;

import com.example.attendance.model.Student;
import com.example.attendance.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.attendance.QRCodeGenerator;

@Controller
@RequestMapping("/admin/students")
public class StudentController {

    private final StudentService studentService;

    // Constructor Injection to link our Service Layer
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 1. Display list of all students
    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list";
    }

    // 2. Show Add Student Form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        return "students/create";
    }

    // 3. Handle saving form data
    @PostMapping
    public String saveStudent(@Valid @ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()) {
            return "students/create";
        }
        studentService.saveStudent(student);
        return "redirect:/admin/students";
    }

    // 4. Show Edit Form filled with current data
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return "redirect:/admin/students";
        }
        model.addAttribute("student", student);
        return "students/edit";
    }

    // 5. Handle the Update request
    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable("id") Long id, @Valid @ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()) {
            return "students/edit";
        }
        studentService.saveStudent(student); // Saves the updated record back to MySQL
        return "redirect:/admin/students";
    }

    // 6. Handle the Delete request
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") Long id) {
        studentService.deleteStudent(id);
        return "redirect:/admin/students";
    }

    // 7. Dynamic QR Pass ID Card Generation (Untangled from Delete mapping context blocks)
    @GetMapping("/idcard/{id}")
    public String showStudentIdCard(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return "redirect:/admin/students";
        }

        // Generate QR code packing the student's unique KID string
        String qrCodeBase64 = QRCodeGenerator.generateQRCodeImageBase64(student.getKid(), 250, 250);

        model.addAttribute("student", student);
        model.addAttribute("qrCode", qrCodeBase64);

        return "students/idcard"; // Maps to templates/students/idcard.html
    }
}