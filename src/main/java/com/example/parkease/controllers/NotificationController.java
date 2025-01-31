package com.example.parkease.controllers;

import com.example.parkease.entities.Notification;
import com.example.parkease.entities.User;
import com.example.parkease.repositories.UserRepository;
import com.example.parkease.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Autowired
    public NotificationController(NotificationService notificationService,  UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

   
    @GetMapping
public String getUserNotifications(Principal principal, Model model) {
    String email = principal.getName();
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<Notification> notifications = notificationService.getUserNotifications(user.getId());

    System.out.println("Fetched Notifications: " + notifications); // Debugging

    model.addAttribute("notifications", notifications);
    return "notifications"; 
}


    @PostMapping("/mark-read")
@ResponseBody
public void markNotificationsAsRead(Principal principal) {
    String email = principal.getName(); // Get email
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    notificationService.markNotificationsAsRead(user.getId());
}

}

