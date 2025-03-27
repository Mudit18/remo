package com.md.remo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.md.remo.model.SuspiciousTransaction;

@Repository
public interface SuspiciousTransactionRepository extends JpaRepository<SuspiciousTransaction, Long> {
    @Query(value = """
        SELECT st.* 
        FROM suspicious_transactions st
        JOIN transactions t 
            ON st.transaction_id = t.id
        WHERE t.user_id = :userId
            AND t.is_active = true
            AND st.resolved = false
        ORDER BY t.timestamp DESC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<SuspiciousTransaction> findSuspiciousTransactionsByUserId(@Param("userId") String userId, @Param("limit") Long limit, @Param("offset") Long offset);

}