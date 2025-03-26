package com.md.remo.controller;

import com.md.remo.dto.TransactionDTO;
import com.md.remo.model.Transaction;
import com.md.remo.service.TransactionService;
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
    public Transaction logTransaction(@RequestBody TransactionDTO transaction) {
        return service.createTransaction(transaction);
    }

    // TODO secure to ensure only the user can fetch their transactions
    @GetMapping("/suspicious/{userId}")
    public List<Transaction> getSuspiciousTransactions(@PathVariable String userId) {
        return service.getSuspiciousTransactions(userId);
    }
}