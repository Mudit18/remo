package com.md.remo.controller;

import com.md.remo.dto.TransactionDTO;
import com.md.remo.model.SuspiciousTransaction;
import com.md.remo.model.Transaction;
import com.md.remo.service.TransactionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction Management API")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/add")
    @Operation(
        summary = "Add a new transaction",
        description = "Creates a new transaction and validates for suspicious activity.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Transaction successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<Transaction> logTransaction(@RequestBody TransactionDTO transaction) {
        try {
            // TODO authorization
            Transaction createdTransaction = service.createTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (Exception e) {
            // TODO add a logger and log exception
            System.out.printf("Error saving transaction for user ID: %s. Exception: %s%n", transaction.getUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getSuspiciousTransactions/{userId}")
    @Operation(
        summary = "Get suspicious transactions",
        description = "Fetches all suspicious transactions for a given user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of suspicious transactions"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<List<SuspiciousTransaction>> getSuspiciousTransactions(@PathVariable String userId, @RequestParam(defaultValue = "0") Long offset) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            // TODO authorization
            List<SuspiciousTransaction> suspiciousTransactions = service.getSuspiciousTransactions(userId, 100L, offset);
            return ResponseEntity.ok(suspiciousTransactions);
        } catch (Exception e) {
            // TODO log using logger
            System.out.printf("Error retrieving suspicious transactions for user ID: %s. Exception: %s%n", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}