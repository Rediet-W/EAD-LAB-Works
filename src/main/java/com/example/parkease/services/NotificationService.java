package com.example.parkease.services;

import com.example.parkease.entities.Booking;
import com.example.parkease.entities.Notification;
import com.example.parkease.entities.User;
import com.example.parkease.repositories.BookingRepository;
import com.example.parkease.repositories.NotificationRepository;
import com.example.parkease.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public void sendNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);

        notificationRepository.save(notification);
    }
@GetMapping
@ResponseBody
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public void markNotificationsAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    // Send a reminder 30 minutes before booking starts**
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void sendUpcomingBookingReminders() {
        System.out.println("🔔 Checking upcoming bookings...");
        LocalDateTime reminderTime = LocalDateTime.now().plusMinutes(30);
        List<Booking> upcomingBookings = bookingRepository.findBookingsStartingAt(reminderTime);

        for (Booking booking : upcomingBookings) {
            sendNotification(booking.getUser().getId(), "Reminder: Your parking starts in 30 minutes!");
        }
    }

    //  Send a reminder 15 minutes before booking ends**
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void sendEndBookingReminders() {
        LocalDateTime reminderTime = LocalDateTime.now().plusMinutes(15);
        List<Booking> endingBookings = bookingRepository.findBookingsEndingAt(reminderTime);

        for (Booking booking : endingBookings) {
            sendNotification(booking.getUser().getId(), "Reminder: Your parking ends in 15 minutes!");
        }
    }

    // send a notification when a payment is made**
    public void notifyPaymentSuccess(Long userId) {
        sendNotification(userId, "Your payment was successful!");
    }
}

