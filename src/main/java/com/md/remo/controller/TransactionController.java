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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
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
        long startTime = System.currentTimeMillis();
        try {
            // TODO authorization
            Transaction createdTransaction = service.createTransaction(transaction);
            long endTime = System.currentTimeMillis();
            log.info("Transaction created successfully for user ID: {}. Time taken: {} ms", transaction.getUserId(), (endTime - startTime));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("Error saving transaction for user ID: {}. Exception: {}. Time taken: {} ms", transaction.getUserId(), e.getMessage(), (endTime - startTime));
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
        long startTime = System.currentTimeMillis();
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            // TODO authorization
            List<SuspiciousTransaction> suspiciousTransactions = service.getSuspiciousTransactions(userId, 100L, offset);
            long endTime = System.currentTimeMillis();
            log.info("Fetched suspicious transactions for user ID: {}. Time taken: {} ms", userId, (endTime - startTime));
            return ResponseEntity.ok(suspiciousTransactions);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("Error retrieving suspicious transactions for user ID: {}. Exception: {}. Time taken: {} ms", userId, e.getMessage(), (endTime - startTime));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getBlockedUsers")
    @Operation(
        summary = "Get blocked users",
        description = "Fetches all users with suspicious transactions.",
        responses = {
            @ApiResponse(responseCode = "200", description = "List of users and their flagged transactions"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    public ResponseEntity<Map<String, List<Transaction>>> getBlockedUserDetails() {
        long startTime = System.currentTimeMillis();
        try {
            // TODO authorization
            List<Transaction> suspiciousTransactions = service.getAllSuspiciousTransactions();
            Map<String, List<Transaction>> blockedUsers = new HashMap<>();
            suspiciousTransactions.forEach((t) -> {
                List<Transaction> userTransactions = blockedUsers.get(t.getUserId());
                if (userTransactions == null) {
                    userTransactions = new ArrayList<>();
                }
                userTransactions.add(t);
                blockedUsers.put(t.getUserId(), userTransactions);
            });
            long endTime = System.currentTimeMillis();
            log.info("Fetched blocked users details. Time taken: {} ms", (endTime - startTime));
            return ResponseEntity.ok(blockedUsers);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("Error fetching blocked users. Exception: {}. Time taken: {} ms", e.getMessage(), (endTime - startTime));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}