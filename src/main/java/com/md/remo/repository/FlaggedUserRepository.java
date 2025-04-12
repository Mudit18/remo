package com.md.remo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.md.remo.model.FlaggedUser;

@Repository
public interface FlaggedUserRepository extends JpaRepository<FlaggedUser, Long> {

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM flagged_users f
            WHERE f.user_id = :userId 
                AND f.is_active = true
        )
    """, nativeQuery = true)
    boolean isUserFlagged(@Param("userId") String userId);

}