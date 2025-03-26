package com.md.remo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.md.remo.dto.TransactionDTO;
import com.md.remo.model.SuspiciousTransaction;
import com.md.remo.model.SuspiciousTransactionType;
import com.md.remo.model.Transaction;
import com.md.remo.model.TransactionType;
import com.md.remo.repository.SuspiciousTransactionRepository;
import com.md.remo.repository.TransactionRepository;
import com.md.remo.utils.transactions.TransactionUtil;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final SuspiciousTransactionRepository suspiciousTransactionRepository;

    public TransactionService(TransactionRepository repository, SuspiciousTransactionRepository suspiciousTransactionRepository) {
        this.transactionRepository = repository;
        this.suspiciousTransactionRepository = suspiciousTransactionRepository;
    }

    // TODO make this robust and transactional so that even 
    // if the suspicious transaction logic fails for some reason, 
    // the transaction should not be added to the DB
    public Transaction createTransaction(TransactionDTO dto) {
        Transaction transaction = Transaction.builder()
            .userId(dto.getUserId())
            .amount(dto.getAmount())
            .timestamp(dto.getTimestamp())
            .lastUpdated(LocalDateTime.now())
            .transactionType(dto.getTransactionType())
            .isActive(true)
            .build();
        try {
            transaction = transactionRepository.save(transaction);
        } catch (Exception e) {
            System.err.printf("Error saving transaction for user ID: %s. Exception: %s%n", transaction.getUserId(), e.getMessage());
            throw new RuntimeException("Failed to create transaction", e);
        }
        if (transaction.getId() != null) {
            validateSuspiciousTransaction(transaction);
            return transaction;
        }
        throw new RuntimeException(String.format("Transaction creation failed for user ID %s", transaction.getUserId()));
    }

    private void validateSuspiciousTransaction(Transaction transaction) {
        List<SuspiciousTransactionType> flaggedSuspicions = new ArrayList<>();

        // Checking for high volume transactions
        if (TransactionUtil.isHighVolumeTransaction(transaction)) {
            System.out.println("High volume transaction detected for user ID: " + transaction.getUserId());
            flaggedSuspicions.add(SuspiciousTransactionType.HIGH_VOLUME_TRANSACTIONS);
        }

        // Checking for frequent small transactions
        long recentTransactions = transactionRepository.getRecentTransactionsBelowThreshold(
            transaction.getUserId(),
            TransactionUtil.FREQUENT_TRANSACTION_TIME_THRESHOLD,
            TransactionUtil.FREQUENT_TRANSACTION_AMOUNT_THRESHOLD
        );
        if (recentTransactions > TransactionUtil.FREQUENT_TRANSACTION_COUNT_THRESHOLD) {
            System.out.println("Frequent small transactions detected for user ID: " + transaction.getUserId());
            flaggedSuspicions.add(SuspiciousTransactionType.FREQUENT_SMALL_TRANSACTIONS);
        }

        // Checking for rapid transfers        
        if (transactionRepository.checkForRapidTransactionsOfType(
            transaction.getUserId(),
            TransactionUtil.RAPID_TRANSFER_TIME_THRESHOLD,
            TransactionUtil.RAPID_TRANSFER_COUNT_THRESHOLD,
            TransactionType.TRANSFER.toString()
        )) {
            System.out.println("Rapid transfers detected for user ID: " + transaction.getUserId());
            flaggedSuspicions.add(SuspiciousTransactionType.RAPID_TRANSFERS);
        }

        // Saving all flagged suspicious transactions
        // TODO based on the business logic, mark related transactions suspicious too
        List<SuspiciousTransaction> suspiciousTransactions = flaggedSuspicions.stream()
            .map(type -> SuspiciousTransaction.builder()
                .transaction_id(transaction.getId())
                .type(type)
                .lastUpdated(LocalDateTime.now())
                .resolved(false)
                .build())
            .collect(Collectors.toList());

        suspiciousTransactionRepository.saveAll(suspiciousTransactions);
    }

    public List<Transaction> getSuspiciousTransactions(String userId, Long limit, Long offset) {
        return transactionRepository.findSuspiciousTransactionsByUserId(userId, limit, offset);
    }
}