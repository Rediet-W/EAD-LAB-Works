package com.example.parkease.repositories;

import com.example.parkease.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find all bookings where the start time is exactly 30 minutes from now
    @Query("SELECT b FROM Booking b WHERE b.startTime = :time AND b.status = 'Active'")
    List<Booking> findBookingsStartingAt(LocalDateTime time);

    // Find all bookings where the end time is exactly 15 minutes from now
    @Query("SELECT b FROM Booking b WHERE b.endTime = :time AND b.status = 'Active'")
    List<Booking> findBookingsEndingAt(LocalDateTime time);

    List<Booking> findByUserId(Long userId);
}
