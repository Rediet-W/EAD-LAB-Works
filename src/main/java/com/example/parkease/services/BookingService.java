package com.example.parkease.services;

import com.example.parkease.entities.Booking;
import com.example.parkease.entities.ParkingSpot;
import com.example.parkease.entities.User;
import com.example.parkease.repositories.BookingRepository;
import com.example.parkease.repositories.ParkingSpotRepository;
import com.example.parkease.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ParkingSpotRepository parkingSpotRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.userRepository = userRepository;
    }
    public double calculateRoundedHours(LocalDateTime startTime, LocalDateTime endTime) {
        double minutes = Duration.between(startTime, endTime).toMinutes();
    
        if (minutes <= 30) {
            return 0.5;
        } else if (minutes <= 60) {
            return 1.0;
        } else if (minutes <= 90) {
            return 1.5;
        } else if (minutes <= 120) {
            return 2.0;
        } else {
            return Math.ceil(minutes / 30.0) / 2.0;
        }
    }
    

    public Booking createBooking(Long userId, Long parkingSpotId, LocalDateTime startTime, LocalDateTime endTime, String vehicleNumber) {
        ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
                .orElseThrow(() -> new RuntimeException("Parking spot not found"));
    
        if (parkingSpot.getAvailableSpots() <= 0) {
            throw new RuntimeException("No available spots for this location.");
        }
    
        if (!parkingSpot.isAvailableForBooking(startTime, endTime)) {
            throw new RuntimeException("Parking spot is already booked for the selected time slot");
        }
    
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        double hours = calculateRoundedHours(startTime, endTime);
        double totalPrice = hours * parkingSpot.getPricePerHour(); 

    
        Booking booking = new Booking();
        booking.setParkingSpot(parkingSpot);
        booking.setUser(user);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setTotalPrice(totalPrice);
        booking.setStatus("Active");
        booking.setVehicleNumber(vehicleNumber);
    
        parkingSpot.decrementAvailableSpots();
        parkingSpotRepository.save(parkingSpot); 
    
        return bookingRepository.save(booking);
    }
    

    public void cancelBooking(Long bookingId, Long userId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    
        if (!isAdmin && !booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own bookings!");
        }
    
        if (LocalDateTime.now().isAfter(booking.getStartTime())) {
            throw new RuntimeException("Booking has already started, cancellation not allowed!");
        }
    
        booking.setStatus("Cancelled");
    
        // Increment available spots when canceling
        ParkingSpot parkingSpot = booking.getParkingSpot();
        parkingSpot.incrementAvailableSpots();
        parkingSpotRepository.save(parkingSpot); 
    
        bookingRepository.save(booking);
    }
    
    

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }


    //payment
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Autowired
    private NotificationService notificationService;
    public void processPayment(Long bookingId, String paymentMethod) {
        Booking booking = getBookingById(bookingId);

        if (!"Pending".equals(booking.getPaymentStatus())) {
            throw new RuntimeException("Payment already processed for this booking.");
        }

        booking.setPaymentStatus("Paid");
        booking.setPaymentMethod(paymentMethod);
        bookingRepository.save(booking);
        notificationService.notifyPaymentSuccess(booking.getUser().getId());
    }

    public void markPaymentAsPaid(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    
        if (!booking.getStatus().equals("Completed")) {
            booking.setPaymentStatus("Paid");
            bookingRepository.save(booking);
        }
    }
    

    public Map<String, Object> getDashboardData(LocalDate startDate, LocalDate endDate) {
    List<Booking> allBookings = bookingRepository.findAll();

    double totalRevenue = allBookings.stream()
            .filter(booking -> "completed".equals(booking.getStatus()))
            .mapToDouble(Booking::getTotalPrice)
            .sum();

    long activeBookings = allBookings.stream().filter(b -> "Active".equals(b.getStatus())).count();
    long completedBookings = allBookings.stream().filter(b -> "Completed".equals(b.getStatus())).count();
    long cancelledBookings = allBookings.stream().filter(b -> "Cancelled".equals(b.getStatus())).count();

    return Map.of(
            "totalRevenue", totalRevenue,
            "activeBookings", activeBookings,
            "completedBookings", completedBookings,
            "cancelledBookings", cancelledBookings,
            "bookings", allBookings.stream()
                    .filter(b -> !b.getStartTime().toLocalDate().isBefore(startDate) &&
                                 !b.getEndTime().toLocalDate().isAfter(endDate))
                    .collect(Collectors.toList())
    );
}
public void completeBooking(Long bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

    if (!booking.getStatus().equals("Active")) {
        throw new RuntimeException("Only active bookings can be completed.");
    }

    booking.setStatus("Completed");

   
    ParkingSpot parkingSpot = booking.getParkingSpot();
    parkingSpot.incrementAvailableSpots();
    parkingSpotRepository.save(parkingSpot);

    bookingRepository.save(booking);
}

}
