package com.md.remo.utils.transactions;

import java.math.BigDecimal;

import com.md.remo.model.Transaction;

public final class TransactionUtil {

    // $ 10000
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
}
