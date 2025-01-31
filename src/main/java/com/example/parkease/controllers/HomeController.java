package com.example.parkease.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/dashboard")
    public String showLandingPage() {
        return "landing"; 
    }

}
