package com.example.parkease.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parking_spot_id", nullable = false)
    private ParkingSpot parkingSpot;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private String status; // Active, Completed, Cancelled

    @Column(nullable = false) 
    private String vehicleNumber;

    @Column(nullable = false)
    private String paymentStatus = "Pending"; // Default is Pending

    @Column(nullable = true)
    private String paymentMethod; // Can be Telebirr, Chapa, or M-Pesa
}
