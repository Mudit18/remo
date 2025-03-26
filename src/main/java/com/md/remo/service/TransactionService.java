package com.md.remo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.md.remo.dto.TransactionDTO;
import com.md.remo.model.Transaction;
import com.md.remo.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction createTransaction(TransactionDTO dto) {
        Transaction transaction = Transaction.builder()
            .userId(dto.getUserId())
            .amount(dto.getAmount())
            .timestamp(dto.getTimestamp())
            .transactionType(dto.getTransactionType())
            .build();
        try {
            transaction = repository.save(transaction);
        } catch (Exception e) {
            // TODO log the exception with the userId (without confidential details)
            throw new RuntimeException("Failed to create transaction", e);
        }
        if (transaction.getId() != null) {
            validateSuspiciousTransaction(transaction);
            return transaction;
        }
        throw new RuntimeException(String.format("Transaction creation failed for user ID %s", transaction.getUserId()));
    }

    public List<Transaction> getSuspiciousTransactions(String userId) {
        return new ArrayList<Transaction>();
    }

    private void validateSuspiciousTransaction(Transaction transaction) {
        // TODO implement
    }
}