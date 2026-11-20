package com.bank.ePon.service;

import com.bank.ePon.model.User;
import com.bank.ePon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User registerUser(String fullName, String mobileNumber, String pin) {
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException("Mobile number is already registered.");
        }
        User newUser = new User(fullName, mobileNumber, pin);
        return userRepository.save(newUser);
    }

    @Transactional
    public User login(String mobileNumber, String pin) {
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("Mobile number does not exist."));

        if (user.isAccountLocked()) {
            throw new RuntimeException("Account is locked due to too many failed login attempts.");
        }

        if (!user.getPin().equals(pin)) {
            int currentAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(currentAttempts);

            if (currentAttempts >= 3) {
                user.setAccountLocked(true);
                userRepository.save(user);
                throw new RuntimeException("Incorrect PIN. Maximum 3 attempts reached. Your account has been locked.");
            }

            userRepository.save(user);

            throw new RuntimeException("Incorrect PIN. You have " + (3 - currentAttempts) + " attempt(s) remaining.");
        }

        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        return user;
    }
}