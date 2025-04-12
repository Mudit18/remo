package com.md.remo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.md.remo.model.SuspiciousTransaction;
import com.md.remo.model.Transaction;
import com.md.remo.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction Management API")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/")
    @Operation(
        summary = "Add a new transaction",
        description = "Creates a new transaction and validates for suspicious activity.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Transaction successfully created"),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input"),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error")
        }
    )
    public ResponseEntity<Transaction> addTransaction(
        @RequestBody Transaction transaction) {
        if (transaction.getUserId() == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }
        try {
            Transaction createdTransaction = service
                .createTransaction(transaction);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTransaction);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    @GetMapping("/users/{userId}/suspicious")
    @Operation(
        summary = "Get suspicious transactions for a user",
        description = "Fetches all suspicious transactions for a given user.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of suspicious transactions"),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid user ID"),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error")
        }
    )
    public ResponseEntity<List<SuspiciousTransaction>> getSuspiciousTransactions(
        @PathVariable String userId, 
        @RequestParam(defaultValue = "0") Long offset
    ) {
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .build();
        }
        try {
            List<SuspiciousTransaction> suspiciousTransactions = 
                service.getSuspiciousTransactions(userId, 100L, offset);
            return ResponseEntity.ok(suspiciousTransactions);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    @GetMapping("/blocked-users")
    @Operation(
        summary = "Get blocked users",
        description = "Fetches all users with suspicious transactions.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of users and their flagged transactions"),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error")
        }
    )
    public ResponseEntity<Map<String, List<Transaction>>> getBlockedUserDetails() {
        try {
            List<Transaction> suspiciousTransactions = service
                .getAllSuspiciousTransactions();
            Map<String, List<Transaction>> blockedUsers = new HashMap<>();
            suspiciousTransactions.forEach((t) -> {
                List<Transaction> userTransactions = blockedUsers.get(t.getUserId());
                if (userTransactions == null) {
                    userTransactions = new ArrayList<>();
                }
                userTransactions.add(t);
                blockedUsers.put(t.getUserId(), userTransactions);
            });
            return ResponseEntity.ok(blockedUsers);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }
}
