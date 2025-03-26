package com.md.remo.service;

import com.md.remo.dto.TransactionDTO;
import com.md.remo.model.Transaction;
import com.md.remo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

        // TODO check for suspicious transactions

        return repository.save(transaction);
    }

    public List<Transaction> getSuspiciousTransactions(String userId) {
        // TODO implement
        return new ArrayList<Transaction>();
    }

    private boolean isSuspicious(Transaction transaction) {
        // TODO Implement
        return false;
    }
}