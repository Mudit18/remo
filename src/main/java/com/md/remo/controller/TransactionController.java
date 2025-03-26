package com.md.remo.controller;

import com.md.remo.dto.TransactionDTO;
import com.md.remo.model.Transaction;
import com.md.remo.service.TransactionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    // TODO secure to ensure only the user can add their transactions
    @PostMapping
    public ResponseEntity<Transaction> logTransaction(@RequestBody TransactionDTO transaction) {
        try {
            // TODO secure to ensure only the user can fetch their transactions
            Transaction createdTransaction = service.createTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (Exception e) {
            // TODO add a logger and log exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/suspicious/{userId}")
    public ResponseEntity<List<Transaction>> getSuspiciousTransactions(@PathVariable String userId) {
        try {
            // TODO secure to ensure only the user can fetch their transactions
            List<Transaction> suspiciousTransactions = service.getSuspiciousTransactions(userId);
            return ResponseEntity.ok(suspiciousTransactions);
        } catch (Exception e) {
            // TODO log exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}