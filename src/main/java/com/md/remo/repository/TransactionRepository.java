package com.md.remo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.md.remo.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


}