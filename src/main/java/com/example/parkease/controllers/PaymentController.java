package com.example.parkease.controllers;

import com.example.parkease.entities.Booking;
import com.example.parkease.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final BookingService bookingService;

    @Autowired
    public PaymentController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Show Payment Page
    @GetMapping("/{bookingId}")
    public String showPaymentPage(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "payments/payment";
    }

    // Simulate Payment (Changes Status)
    @PostMapping("/pay")
    public String processPayment(
            @RequestParam Long bookingId,
            @RequestParam String paymentMethod) {

        bookingService.processPayment(bookingId, paymentMethod);
        return "redirect:/bookings/user?userId=" + bookingService.getBookingById(bookingId).getUser().getId();
    }
    
}
