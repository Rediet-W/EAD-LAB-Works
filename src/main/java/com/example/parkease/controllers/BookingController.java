package com.example.parkease.controllers;

import com.example.parkease.entities.Booking;
import com.example.parkease.entities.ParkingSpot;
import com.example.parkease.entities.User;
import com.example.parkease.services.BookingService;
import com.example.parkease.services.ParkingSpotService;
import com.example.parkease.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    
    private final BookingService bookingService;
    private final ParkingSpotService parkingSpotService;
    private final UserService userService;

    @Autowired
    public BookingController(BookingService bookingService, 
                             ParkingSpotService parkingSpotService, 
                             UserService userService) {
        this.bookingService = bookingService;
        this.parkingSpotService = parkingSpotService;
        this.userService = userService;
    }

    /**
     * This method shows the booking form.
     * It retrieves the current user from Principal, sets the userId in session (if not set)
     * and adds the userId to the model.
     */
    @GetMapping("/create")
    public String showBookingForm(
            @RequestParam Long spotId, 
            @RequestParam String startTime, 
            @RequestParam String endTime, 
            Model model, 
            Principal principal,
            HttpSession session
    ) {
        // Retrieve the user using the principal
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Set the userId in the session if not already present
        if (session.getAttribute("userId") == null) {
            session.setAttribute("userId", user.getId());
        }
        
        // Add userId to the model for the navbar
        model.addAttribute("userId", user.getId());
        model.addAttribute("parkingSpotId", spotId);
        ParkingSpot parkingSpot = parkingSpotService.getParkingSpotById(spotId);
        model.addAttribute("parkingSpot", parkingSpot);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        return "bookings/create";
    }

    /**
     * This method displays available parking spots.
     * Since it renders a page that may include the navbar,
     * we add the userId from the session to the model.
     */
    @GetMapping("/available")
    public String showAvailableSpots(@RequestParam String startTime, 
                                     @RequestParam String endTime, 
                                     Model model,
                                     HttpSession session) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);

        List<ParkingSpot> availableSpots = parkingSpotService.findAvailableSpots(start, end);
        model.addAttribute("parkingSpots", availableSpots);
        
        // Add userId to the model if available in session
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("userId", userId);
        }

        return "parking-spots/available";
    }

    /**
     * This method confirms a booking.
     * The userId is passed as a request parameter in this case (from the form).
     * We add it to the model in case of errors.
     */
    @PostMapping("/confirm")
    public String confirmBooking(
            @RequestParam Long userId,
            @RequestParam Long parkingSpotId,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String vehicleNumber,
            Model model,
            HttpSession session
    ) {
        session.setAttribute("userId", userId);
        model.addAttribute("userId", userId);

        try {
            Booking booking = bookingService.createBooking(
                    userId,
                    parkingSpotId,
                    LocalDateTime.parse(startTime),
                    LocalDateTime.parse(endTime),
                    vehicleNumber
            );
            return "redirect:/payments/" + booking.getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("parkingSpotId", parkingSpotId);
            model.addAttribute("startTime", startTime);
            model.addAttribute("endTime", endTime);
            model.addAttribute("vehicleNumber", vehicleNumber);
            return "bookings/create";
        }
    }

    /**
     * This method displays the user's bookings.
     * It retrieves the userId from the session and adds it to the model.
     */
    @GetMapping("/user")
    public String viewUserBookings(@RequestParam(required = false) Long userId, Model model, HttpSession session) {
        // Prefer the userId from the session if available
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId != null) {
            userId = sessionUserId;
        } else if(userId != null) {
            session.setAttribute("userId", userId);
        }
        model.addAttribute("userId", userId);

        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        model.addAttribute("bookings", bookings);
        return "bookings/user";
    }

    /**
     * This method displays all bookings for admin.
     * Even though admin might not require a userId for personalized data,
     * it is added for consistency (if the navbar requires it).
     */
    @GetMapping("/admin")
    public String viewAllBookings(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("userId", userId);
        }
        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "bookings/admin";
    }

    /**
     * This method cancels a booking for a user.
     * It retrieves the user via Principal and ensures the userId is available.
     */
    @GetMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, Principal principal, Model model, HttpSession session) {
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update session and model with userId
        session.setAttribute("userId", user.getId());
        model.addAttribute("userId", user.getId());

        try {
            bookingService.cancelBooking(id, user.getId(), false);
            return "redirect:/bookings/user?userId=" + user.getId();
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "bookings/user";
        }
    }


    @GetMapping("/admin/cancel/{id}")
    public String adminCancelBooking(@PathVariable Long id, HttpSession session, Model model) {
        // Optionally add the userId from the session (if any) for consistency
        Long userId = (Long) session.getAttribute("userId");
        if(userId != null) {
            model.addAttribute("userId", userId);
        }
        bookingService.cancelBooking(id, null, true);
        return "redirect:/bookings/admin";
    }
}
