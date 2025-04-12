package com.md.remo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.md.remo.model.FlaggedUser;
import com.md.remo.model.SuspiciousTransaction;
import com.md.remo.model.SuspiciousTransactionType;
import com.md.remo.model.Transaction;
import com.md.remo.repository.FlaggedUserRepository;
import com.md.remo.repository.SuspiciousTransactionRepository;
import com.md.remo.repository.TransactionRepository;
import com.md.remo.utils.transactions.TransactionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final SuspiciousTransactionRepository suspiciousTransactionRepository;

    private final FlaggedUserRepository flaggedUserRepository;

    public TransactionService(TransactionRepository repository, 
        SuspiciousTransactionRepository suspiciousTransactionRepository,
        FlaggedUserRepository flaggedUserRepository) {
        this.transactionRepository = repository;
        this.suspiciousTransactionRepository = suspiciousTransactionRepository;
        this.flaggedUserRepository = flaggedUserRepository;
    }

    /* 
        Transactional method to ensure that transaction will not be saved 
        if the suspicious transaction validation fails. 
    */
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        try {
            boolean userFlagged = flaggedUserRepository
                .isUserFlagged(transaction.getUserId());

            if (userFlagged) {
                log.error("User account is flagged and temporarily blocked for user ID: {}", transaction.getUserId());
                throw new RuntimeException(
                    "User account is flagged and temporarily blocked."
                );
            }

            // Create and save the transaction
            Transaction savedTransaction = saveTransaction(transaction);

            if (savedTransaction.getId() != null) {
                validateSuspiciousTransaction(savedTransaction);
                return savedTransaction;
            } else {
                log.error("Failed to create transaction for user ID: {}", transaction.getUserId());
                throw new DataAccessException("Failed to create transaction") {};
            }
        } catch (DataAccessException e) {
            log.error(
                "Error saving transaction for user ID: {}. Exception: {}", 
                transaction.getUserId(), e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            log.error(
                "Unexpected error occurred while creating transaction for user ID: {}. Exception: {}", 
                transaction.getUserId(), e
            );
            throw e;
        }
    }

    private Transaction saveTransaction(Transaction transaction) {
        Transaction newTransaction = Transaction.builder()
            .userId(transaction.getUserId())
            .amount(transaction.getAmount())
            .timestamp(transaction.getTimestamp())
            .createdAt(LocalDateTime.now())
            .lastUpdated(LocalDateTime.now())
            .transactionType(transaction.getTransactionType())
            .isActive(true)
            .build();
        return transactionRepository.save(newTransaction);
    }

    private void validateSuspiciousTransaction(Transaction transaction) {
        // Get the suspicious transactions
        List<SuspiciousTransaction> flaggedSuspicions = 
            identifyFlaggedTransactions(transaction);

        suspiciousTransactionRepository.saveAll(flaggedSuspicions);

        if (!flaggedSuspicions.isEmpty()) {
            flagUser(transaction.getUserId());
        }
    }

    private void flagUser(String userId) {
        FlaggedUser flaggedUser = FlaggedUser.builder()
            .userId(userId)
            .isActive(true)
            .flaggedAt(LocalDateTime.now())
            .build();
        flaggedUserRepository.save(flaggedUser);
    }

    private List<SuspiciousTransaction> identifyFlaggedTransactions(
        Transaction transaction) {
        List<SuspiciousTransaction> flaggedSuspicions = new ArrayList<>();

        // High Volume Transactions
        if (TransactionUtil.isHighVolumeTransaction(transaction)) {
            logSuspiciousTransaction(
                "High volume transaction",
                transaction.getUserId()
            );
            flaggedSuspicions.add(
                TransactionUtil.createSuspiciousTransaction(
                    SuspiciousTransactionType.HIGH_VOLUME_TRANSACTIONS,
                    transaction
                )
            );
        }
        // Fetching the recent transactions from the last one hour
        List<Transaction> recentTransactions = transactionRepository
            .getRecentTransactions(
                transaction.getUserId(),
                TransactionUtil.FREQUENT_TRANSACTION_TIME_THRESHOLD,
                transaction.getTimestamp()
            );

        // Frequent Small Transactions
        List<SuspiciousTransaction> freqSmallTxns = TransactionUtil.
            checkFrequentSmallTransactions(recentTransactions);
        if (!freqSmallTxns.isEmpty()) {
            logSuspiciousTransaction(
                "Frequent small transactions",
                transaction.getUserId()
            );
            flaggedSuspicions.addAll(freqSmallTxns);
        }

        // Rapid Transfers
        List<SuspiciousTransaction> rapidTransfers = TransactionUtil.
            checkRapidTransfers(recentTransactions);
        if (!rapidTransfers.isEmpty()) {
            logSuspiciousTransaction(
                "Rapid transfers",
                transaction.getUserId()
            );
            flaggedSuspicions.addAll(rapidTransfers);
        }

        return flaggedSuspicions;
    }

    private void logSuspiciousTransaction(String description, String userId) {
        log.info("{} detected for user ID: {}", description, userId);
    }

    public List<SuspiciousTransaction> getSuspiciousTransactions(
        String userId, Long limit, Long offset) {
        return suspiciousTransactionRepository
            .findSuspiciousTransactionsByUserId(userId, limit, offset);
    }

    public List<Transaction> getAllSuspiciousTransactions() {
        return transactionRepository
            .findSuspiciousTransactions();
    }

    public Map<String, List<Transaction>> getBlockedUserDetails() {
        List<Transaction> suspiciousTransactions = getAllSuspiciousTransactions();
        Map<String, List<Transaction>> blockedUsers = new HashMap<>();
        suspiciousTransactions.forEach((t) -> {
            List<Transaction> userTransactions = blockedUsers.get(t.getUserId());
            if (userTransactions == null) {
                userTransactions = new ArrayList<>();
            }
            userTransactions.add(t);
            blockedUsers.put(t.getUserId(), userTransactions);
        });
        return blockedUsers;
    }
}