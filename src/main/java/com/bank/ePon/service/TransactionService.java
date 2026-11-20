package com.bank.ePon.service;

import com.bank.ePon.model.Transaction;
import com.bank.ePon.model.User;
import com.bank.ePon.repository.TransactionRepository;
import com.bank.ePon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public void cashIn(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cash-in amount must be a positive value.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        Transaction tx = new Transaction(
                "Cash-In",
                amount,
                "Cash-in deposit via OTC/Online",
                LocalDateTime.now(),
                user
        );
        transactionRepository.save(tx);
    }

    @Transactional
    public void transferMoney(Long senderId, String receiverMobile, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found."));

        if (sender.getMobileNumber().equals(receiverMobile)) {
            throw new IllegalArgumentException("You cannot transfer money to your own mobile number.");
        }

        User receiver = userRepository.findByMobileNumber(receiverMobile)
                .orElseThrow(() -> new RuntimeException("Receiver's mobile number does not exist."));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance for this transfer.");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        Transaction senderTx = new Transaction(
                "Transfer Money",
                amount,
                "Transferred to " + receiverMobile,
                LocalDateTime.now(),
                sender
        );
        transactionRepository.save(senderTx);

        Transaction receiverTx = new Transaction(
                "Receive Money",
                amount,
                "Received from " + sender.getMobileNumber(),
                LocalDateTime.now(),
                receiver
        );
        transactionRepository.save(receiverTx);
    }

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByDateTimeDesc(userId);
    }
}