package com.md.remo.utils.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.md.remo.model.SuspiciousTransaction;
import com.md.remo.model.SuspiciousTransactionType;
import com.md.remo.model.Transaction;
import com.md.remo.model.TransactionType;

public final class TransactionUtil {

    // $10000
    public static final Integer HIGH_VOLUME_TRANSACTION_AMOUNT_THRESHOLD = 10000;

    // 10 transactions
    public static final Integer FREQUENT_TRANSACTION_COUNT_THRESHOLD = 10;
    
    // $100
    public static final Integer FREQUENT_TRANSACTION_AMOUNT_THRESHOLD = 100;

    // In minutes
    public static final Integer FREQUENT_TRANSACTION_TIME_THRESHOLD = 60;

    // In minutes
    public static final Integer RAPID_TRANSFER_TIME_THRESHOLD = 5;

    // 3 transactions
    public static final Integer RAPID_TRANSFER_COUNT_THRESHOLD = 3;

    public static boolean isHighVolumeTransaction(Transaction transaction) {
        BigDecimal threshold = BigDecimal.valueOf(HIGH_VOLUME_TRANSACTION_AMOUNT_THRESHOLD);
        return transaction.getAmount().compareTo(threshold) > 0;
    }

    public static List<SuspiciousTransaction> checkFrequentSmallTransactions(
        List<Transaction> recentTransactions) {

        List<SuspiciousTransaction> flaggedTransactions = new ArrayList<>();
        BigDecimal amountThreshold = BigDecimal.valueOf(FREQUENT_TRANSACTION_AMOUNT_THRESHOLD);
        
        if (recentTransactions.size() > FREQUENT_TRANSACTION_COUNT_THRESHOLD) {
            for (Transaction transaction : recentTransactions) {
                if (transaction.getAmount().compareTo(amountThreshold) < 0) {
                    flaggedTransactions.add(
                        createSuspiciousTransaction(
                            SuspiciousTransactionType.FREQUENT_SMALL_TRANSACTIONS, 
                            transaction
                        ));
                }
            }
        }
        return flaggedTransactions;
    }

    public static List<SuspiciousTransaction> checkRapidTransfers(
        List<Transaction> recentTransactions) {

        List<SuspiciousTransaction> flaggedTransactions = new ArrayList<>();
        LocalDateTime latestTransaction = recentTransactions.get(0).getTimestamp();
        LocalDateTime thresholdTime = latestTransaction.minusMinutes(RAPID_TRANSFER_TIME_THRESHOLD);

        List<Transaction> consecutiveTransactions = recentTransactions.stream()
            .filter(transaction -> !transaction.getTimestamp().isBefore(thresholdTime))
            .limit(RAPID_TRANSFER_COUNT_THRESHOLD)
            .collect(Collectors.toList());
        
        if (consecutiveTransactions.size() == RAPID_TRANSFER_COUNT_THRESHOLD 
            && consecutiveTransactions
                .stream()
                .allMatch(transaction -> 
                    transaction.getTransactionType() == TransactionType.TRANSFER
                )) {
            for (Transaction transaction : consecutiveTransactions) {
                flaggedTransactions.add(createSuspiciousTransaction(SuspiciousTransactionType.RAPID_TRANSFERS, transaction));
            }
        }
        return flaggedTransactions;
    }

    public static SuspiciousTransaction createSuspiciousTransaction(
        SuspiciousTransactionType type, Transaction transaction) {
        return SuspiciousTransaction.builder()
            .transaction_id(transaction.getId())
            .type(type)
            .resolved(false)
            .flaggedAt(LocalDateTime.now())
            .lastUpdated(LocalDateTime.now())
            .build();
    }
    
}
