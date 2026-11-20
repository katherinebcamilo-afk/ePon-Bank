package com.bank.ePon.service;

import com.bank.ePon.model.User;
import com.bank.ePon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    public BigDecimal getCurrentBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User session or account not found."));

        return user.getBalance();
    }
}