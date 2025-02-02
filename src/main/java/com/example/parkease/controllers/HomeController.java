package com.example.parkease.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/dashboard")
    public String showLandingPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId != null) {
            model.addAttribute("userId", userId);
        }
        return "landing"; 
    }
}

