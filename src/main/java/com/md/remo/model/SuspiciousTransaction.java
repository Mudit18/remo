package com.md.remo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "suspicious_transactions")
public class SuspiciousTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long transaction_id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SuspiciousTransactionType type;

    @Column(nullable = false)
    private Boolean resolved;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
