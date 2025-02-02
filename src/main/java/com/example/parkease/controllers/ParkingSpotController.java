package com.example.parkease.controllers;

import com.example.parkease.entities.ParkingSpot;
import com.example.parkease.services.ParkingSpotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/parking-spots")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    @Autowired
    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    // This endpoint shows the time selection page.
    @GetMapping("/select-time")
    public String showTimeSelection(HttpSession session, Model model) {
        // If a userId exists in the session, add it to the model
        Long userId = (Long) session.getAttribute("userId");
        if(userId != null) {
            model.addAttribute("userId", userId);
        }
        return "bookings/select-time"; // Shows select-time.html
    }

    // Show available spots based on selected time
    @GetMapping("/available")
    public String showAvailableSpots(
            @RequestParam String startTime,
            @RequestParam String endTime,
            Model model,
            HttpSession session
    ) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);

        List<ParkingSpot> availableSpots = parkingSpotService.findAvailableSpots(start, end);

        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("parkingSpots", availableSpots);

        // Add userId to the model if available in session
        Long userId = (Long) session.getAttribute("userId");
        if(userId != null) {
            model.addAttribute("userId", userId);
        }
        return "parking-spots/available"; // Shows available.html
    }

    // Admin: View All Parking Spots
    @GetMapping("/admin")
    public String viewAllParkingSpots(Model model, HttpSession session) {
        // Add userId to the model if available in session (for navbar consistency)
        Long userId = (Long) session.getAttribute("userId");
        if(userId != null) {
            model.addAttribute("userId", userId);
        }
        model.addAttribute("parkingSpots", parkingSpotService.getAllParkingSpots());
        return "parking-spots/list";
    }

    // Admin: Show Add Parking Spot Form
    @GetMapping("/admin/add")
    public String showAddParkingSpotForm(Model model, HttpSession session) {
        // Add userId to the model if available in session
        Long userId = (Long) session.getAttribute("userId");
        if(userId != null) {
            model.addAttribute("userId", userId);
        }
        model.addAttribute("parkingSpot", new ParkingSpot());
        return "parking-spots/add";
    }

    // Admin: Add a New Parking Spot
    @PostMapping("/admin")
    public String addParkingSpot(@ModelAttribute ParkingSpot parkingSpot) {
        parkingSpotService.addParkingSpot(parkingSpot);
        return "redirect:/parking-spots/admin";
    }

    // Admin: Show Edit Parking Spot Form
    @GetMapping("/admin/edit/{id}")
    public String showEditParkingSpotForm(@PathVariable Long id, Model model, HttpSession session) {
        ParkingSpot parkingSpot = parkingSpotService.getParkingSpotById(id);
        model.addAttribute("parkingSpot", parkingSpot);
        // Add userId to the model if available in session
        Long userId = (Long) session.getAttribute("userId");
        if(userId != null) {
            model.addAttribute("userId", userId);
        }
        return "parking-spots/edit";
    }

    // Admin: Update a Parking Spot
    @PostMapping("/admin/update/{id}")
    public String updateParkingSpot(@PathVariable Long id, @ModelAttribute ParkingSpot updatedSpot) {
        parkingSpotService.updateParkingSpot(id, updatedSpot);
        return "redirect:/parking-spots/admin";
    }

    // Admin: Delete a Parking Spot
    @GetMapping("/admin/delete/{id}")
    public String deleteParkingSpot(@PathVariable Long id) {
        parkingSpotService.deleteParkingSpot(id);
        return "redirect:/parking-spots/admin";
    }
}
