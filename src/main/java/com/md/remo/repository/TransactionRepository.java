package com.md.remo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.md.remo.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
        SELECT COUNT(*) 
        FROM transactions t
        WHERE t.user_id = :userId 
            AND t.timestamp > (:transactionTimestamp :: timestamp - (:timeThreshold * INTERVAL '1 minute'))
            AND t.amount < :amountThreshold
            AND t.is_active = true
    """, nativeQuery = true)
    long getRecentTransactionsBelowThreshold(@Param("userId") String userId, 
                                             @Param("timeThreshold") Integer timeThreshold, 
                                             @Param("amountThreshold") Integer amountThreshold, 
                                             @Param("transactionTimestamp") LocalDateTime transactionTimestamp);

    @Query(value = """
        SELECT *
        FROM transactions t
        WHERE t.user_id = :userId
            AND t.timestamp > (:transactionTimestamp :: timestamp - (:timeThreshold * INTERVAL '1 minute'))
            AND t.is_active = true
        ORDER BY t.timestamp DESC
        LIMIT :countThreshold
    """, nativeQuery = true)
    List<Transaction> getLastNTransactionsInPeriod(@Param("userId") String userId, 
                                @Param("timeThreshold") Integer timeThreshold, 
                                @Param("countThreshold") Integer countThreshold,
                                @Param("transactionTimestamp") LocalDateTime transactionTimestamp);


    @Query(value = """
        SELECT t.* 
        FROM suspicious_transactions st
        JOIN transactions t 
            ON st.transaction_id = t.id
        WHERE st.resolved = false
    """, nativeQuery = true)
    List<Transaction> findSuspiciousTransactions();

}