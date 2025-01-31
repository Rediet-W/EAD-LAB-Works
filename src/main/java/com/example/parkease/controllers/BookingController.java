package com.example.parkease.controllers;

import com.example.parkease.entities.Booking;
import com.example.parkease.entities.ParkingSpot;
import com.example.parkease.entities.User;
import com.example.parkease.services.BookingService;
import com.example.parkease.services.ParkingSpotService;
import com.example.parkease.services.UserService;

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
    public BookingController(BookingService bookingService, ParkingSpotService parkingSpotService, UserService userService) {
        this.bookingService = bookingService;
        this.parkingSpotService = parkingSpotService;
        this.userService = userService;
    }

    @GetMapping("/create")
public String showBookingForm(
    @RequestParam Long spotId, 
    @RequestParam String startTime, 
    @RequestParam String endTime, 
    Model model, 
    Principal principal
) {
    User user = userService.getUserByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

 
    ParkingSpot parkingSpot = parkingSpotService.getParkingSpotById(spotId);

    
    model.addAttribute("userId", user.getId());
    model.addAttribute("parkingSpotId", spotId); 
    model.addAttribute("parkingSpot", parkingSpot); 
    model.addAttribute("startTime", startTime);
    model.addAttribute("endTime", endTime);

    return "bookings/create"; 
}

    


    @GetMapping("/available")
    public String showAvailableSpots(@RequestParam String startTime, @RequestParam String endTime, Model model) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);

        List<ParkingSpot> availableSpots = parkingSpotService.findAvailableSpots(start, end);
        model.addAttribute("parkingSpots", availableSpots);

        return "parking-spots/available";
    }


    @PostMapping("/confirm")
    public String confirmBooking(
            @RequestParam Long userId,
            @RequestParam Long parkingSpotId,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String vehicleNumber,
            Model model
    ) {
        System.out.println("Received parkingSpotId: " + parkingSpotId); 
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
            model.addAttribute("userId", userId);
            model.addAttribute("parkingSpotId", parkingSpotId);
            model.addAttribute("startTime", startTime);
            model.addAttribute("endTime", endTime);
            model.addAttribute("vehicleNumber", vehicleNumber);
            return "bookings/create"; 
        }
    }
    
@GetMapping("/user")
public String viewUserBookings(@RequestParam Long userId, Model model) {
    List<Booking> bookings = bookingService.getBookingsByUser(userId);
    model.addAttribute("bookings", bookings);
    return "bookings/user";
}

@GetMapping("/admin")
public String viewAllBookings(Model model) {
    List<Booking> bookings = bookingService.getAllBookings();
    model.addAttribute("bookings", bookings);
    return "bookings/admin";
}

@GetMapping("/cancel/{id}")
public String cancelBooking(@PathVariable Long id, Principal principal, Model model) {
    User user = userService.getUserByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

    try {
        bookingService.cancelBooking(id, user.getId(), false);
        return "redirect:/bookings/user?userId=" + user.getId();
    } catch (RuntimeException e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "bookings/user"; 
    }
}

@GetMapping("/admin/cancel/{id}")
public String adminCancelBooking(@PathVariable Long id) {
    bookingService.cancelBooking(id, null, true);
    return "redirect:/bookings/admin";
}


}
