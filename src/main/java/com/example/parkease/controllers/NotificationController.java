package com.example.parkease.controllers;

import com.example.parkease.entities.Notification;
import com.example.parkease.entities.User;
import com.example.parkease.repositories.UserRepository;
import com.example.parkease.services.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Autowired
    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // JSON endpoint for fetching notifications for the logged-in user
    @GetMapping("/list")
    public List<Notification> getUserNotifications(Principal principal, HttpSession session) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        // Optionally store userId in session if needed
        if (session.getAttribute("userId") == null) {
            session.setAttribute("userId", user.getId());
        }
        return notificationService.getUserNotifications(user.getId());
    }

    // You can also expose an endpoint to mark notifications as read if needed.
    @PostMapping("/mark-read")
    public void markNotificationsAsRead(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
        notificationService.markNotificationsAsRead(user.getId());
    }
}
