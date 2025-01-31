package com.example.parkease.services;

import com.example.parkease.entities.ParkingSpot;
import com.example.parkease.repositories.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    @Autowired
    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    public ParkingSpot addParkingSpot(ParkingSpot parkingSpot) {
        return parkingSpotRepository.save(parkingSpot);
    }

    public List<ParkingSpot> getAllParkingSpots() {
        return parkingSpotRepository.findAll();
    }

    public ParkingSpot getParkingSpotById(Long id) {
        return parkingSpotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking Spot not found"));
    }
    
public ParkingSpot getParkingSpotByIdOrThrow(Long id) {
    return parkingSpotRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Parking spot not found"));
}
    public List<ParkingSpot> findAvailableSpots(LocalDateTime startTime, LocalDateTime endTime) {
        return parkingSpotRepository.findAll().stream()
                .filter(spot -> spot.isAvailableForBooking(startTime, endTime))
                .collect(Collectors.toList());
    }
    public ParkingSpot updateParkingSpot(Long id, ParkingSpot updatedSpot) {
        return parkingSpotRepository.findById(id).map(spot -> {
            spot.setLocation(updatedSpot.getLocation());
            spot.setType(updatedSpot.getType());
            spot.setPricePerHour(updatedSpot.getPricePerHour());
    
            if (updatedSpot.getAvailableSpots() >= 0) {
                spot.setAvailableSpots(updatedSpot.getAvailableSpots());
            }
    
            return parkingSpotRepository.save(spot);
        }).orElseThrow(() -> new RuntimeException("Parking Spot not found"));
    }
    

    public void deleteParkingSpot(Long id) {
        parkingSpotRepository.deleteById(id);
    }
}
