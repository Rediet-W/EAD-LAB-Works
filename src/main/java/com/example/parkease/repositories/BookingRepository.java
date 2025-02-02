package com.example.parkease.repositories;

import com.example.parkease.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByVehicleNumberContainingIgnoreCase(String vehicleNumber);
    
    List<Booking> findAllByOrderByIdDesc();

   @Query("SELECT b FROM Booking b WHERE b.startTime BETWEEN :start AND :end")
    List<Booking> findBookingsStartingBetween(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.endTime BETWEEN :start AND :end")
    List<Booking> findBookingsEndingBetween(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    List<Booking> findByUserId(Long userId);
    @Query("SELECT b FROM Booking b WHERE b.status = 'active' AND b.endTime <= :now")
    List<Booking> findActiveBookingsEndedBefore(LocalDateTime now);
}
