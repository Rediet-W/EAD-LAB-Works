package com.example.parkease.services;

import com.example.parkease.entities.User;
import com.example.parkease.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean registerUser(String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false; 
        }
        
        User user = new User(); 
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        user.setRole(role);
        
        userRepository.save(user);
        return true;
    }
    

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole())
            .build();
    }
}
