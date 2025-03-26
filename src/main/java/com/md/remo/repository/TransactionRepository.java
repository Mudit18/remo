package com.md.remo.repository;

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
            AND t.timestamp > NOW() - (:timeThreshold * INTERVAL '1 minute')
            AND t.amount < :amountThreshold
            AND t.is_active = true
    """, nativeQuery = true)
    long getRecentTransactionsBelowThreshold(@Param("userId") String userId, 
                                             @Param("timeThreshold") Integer timeThreshold, 
                                             @Param("amountThreshold") Integer amountThreshold);

    @Query(value = """
        SELECT COUNT(*) > :countThreshold
        FROM transactions t
        WHERE t.user_id = :userId
            AND t.timestamp > NOW() - (:timeThreshold * INTERVAL '1 minute')
            AND t.transaction_type = :transactionType
            AND t.is_active = true
    """, nativeQuery = true)
    boolean checkForRapidTransactionsOfType(@Param("userId") String userId, 
                                             @Param("timeThreshold") Integer timeThreshold, 
                                             @Param("countThreshold") Integer countThreshold,
                                             @Param("transactionType") String transactionType);

    @Query(value = """
        SELECT t.* 
        FROM suspicious_transactions st
        JOIN transactions t 
            ON st.transaction_id = t.id
        WHERE t.user_id = :userId
            AND t.is_active = true
            AND st.resolved = false
        ORDER BY t.timestamp DESC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<Transaction> findSuspiciousTransactionsByUserId(@Param("userId") String userId, @Param("limit") Long limit, @Param("offset") Long offset);

}