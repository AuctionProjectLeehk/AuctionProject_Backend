package com.leehk.auction.domain.wallet.infrastructure;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import com.leehk.auction.domain.wallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_transactions")
@Getter
@NoArgsConstructor
public class WalletTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    @JsonBackReference
    private WalletEntity wallet;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private long amount;

    private LocalDateTime createdAt;

    @Builder
    public WalletTransactionEntity(WalletEntity wallet, TransactionType transactionType, long amount, LocalDateTime createdAt) {
        this.wallet = wallet;
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public void assignWallet(WalletEntity wallet) {
        this.wallet = wallet;
    }

    public void updateFromDomain(WalletTransaction walletTransaction) {
        this.transactionType = walletTransaction.getTransactionType();
        this.amount = walletTransaction.getMoney().getAmount();
        this.createdAt = walletTransaction.getCreatedAt();
    }
}
