package com.example.parkease.controllers;

import com.example.parkease.entities.Booking;
import com.example.parkease.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final BookingService bookingService;

    @Autowired
    public AdminPaymentController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // View All Payments (Admin)
    @GetMapping
    public String viewAllPayments(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "admin/payments"; 
    }

    // View Single Payment Details
    @GetMapping("/{bookingId}")
    public String viewPaymentDetails(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "admin/payment-details"; 
    }

    //  Mark Payment as Completed
    @PostMapping("/mark-paid/{bookingId}")
    public String markPaymentAsPaid(@PathVariable Long bookingId) {
        bookingService.markPaymentAsPaid(bookingId);
        return "redirect:/admin/payments"; 
    }
}
