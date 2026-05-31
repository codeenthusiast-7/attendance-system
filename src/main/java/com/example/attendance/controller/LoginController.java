package com.example.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Renders our upcoming custom stylized login page interface layout
    @GetMapping("/login")
    public String showCustomLoginPage() {
        return "login"; // Maps cleanly to templates/login.html
    }
}