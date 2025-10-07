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
    private final UUID walletId;
    private final Money money;
    private final TransactionType type;
    private final LocalDateTime createdAt;

    public static WalletTransaction create(UUID walletId, Money money, TransactionType type) {
        return WalletTransaction.builder()
                .walletId(walletId)
                .money(money)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
