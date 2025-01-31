package com.example.parkease.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String type; // Example: Basement, outdoor 

    @Column(nullable = false)
    private Double pricePerHour;

    @Column(nullable = false)
    private int availableSpots; 
    @OneToMany(mappedBy = "parkingSpot", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    // 🟢 Method to check if a spot is available
    public boolean isAvailableForBooking(LocalDateTime startTime, LocalDateTime endTime) {
        if (this.availableSpots <= 0) return false; // If no spots left, return false

        for (Booking booking : bookings) {
            if (booking.getStatus().equals("Active") &&
                startTime.isBefore(booking.getEndTime()) &&
                endTime.isAfter(booking.getStartTime())) {
                return false; // Spot is occupied during this time
            }
        }
        return true;
    }

    public void decrementAvailableSpots() {
        if (this.availableSpots > 0) {
            this.availableSpots--;
        }
    }

    public void incrementAvailableSpots() {
        this.availableSpots++;
    }
}