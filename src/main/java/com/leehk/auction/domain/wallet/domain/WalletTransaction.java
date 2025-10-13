package com.leehk.auction.domain.wallet.domain;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.wallet.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class WalletTransaction {
    private final UUID id;
    private final UUID walletId;
    private final Money money;
    private final TransactionType transactionType;
    private final LocalDateTime createdAt;

    private WalletTransaction(UUID id, UUID walletId, Money money, TransactionType transactionType, LocalDateTime localDateTime) {
        this.id = id;
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.money = money;
        this.createdAt = LocalDateTime.now();
    }

    public static WalletTransaction create(UUID walletId, Money money, TransactionType transactionType) {
        return new WalletTransaction(UUID.randomUUID(), walletId, money, transactionType, LocalDateTime.now());
    }
}
