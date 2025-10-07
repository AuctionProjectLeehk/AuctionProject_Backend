package com.leehk.auction.domain.wallet.converter;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.wallet.domain.Wallet;
import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import com.leehk.auction.domain.wallet.infrastructure.WalletEntity;

import java.util.List;
import java.util.stream.Collectors;

public class WalletConverter {

    public static Wallet entityToDomain(WalletEntity walletEntity) {
        Wallet wallet = Wallet.builder()
                .publicId(walletEntity.getPublicId())
                .userId(walletEntity.getUserId())
                .wallName(walletEntity.getWalletName())
                .build();

        // 상태 복원
        switch (walletEntity.getWalletStatus()) {
            case ACTIVE -> wallet.activate();
            case SUSPENDED -> wallet.suspend();
            case CLOSED -> wallet.close();
        }

        // 잔액
        wallet.deposit(new Money(walletEntity.getMoney().getAmount()));

        // Transaction 복원
        if (walletEntity.getTransactions() != null && !walletEntity.getTransactions().isEmpty()) {
            List<WalletTransaction> walletTransactions = walletEntity.getTransactions().stream()
                    .map(WalletTransactionConverter::entityToDomain)
                    .collect(Collectors.toList());
            wallet.getTransactions().addAll(walletTransactions);
        }

        return wallet;
    }

    public static WalletEntity domainToEntity(Wallet wallet) {
        WalletEntity walletEntity = WalletEntity.builder()
                .publicId(wallet.getPublicId())
                .walletName(wallet.getWalletName())
                .userId(wallet.getUserId())
                .money(wallet.getMoney())
                .walletStatus(wallet.getWalletStatus())
                .createdAt(wallet.getCreatedAt())
                .transactions(wallet.getTransactions().stream()
                        .map(transaction -> WalletTransactionConverter.domainToEntity(transaction, null))
                        .collect(Collectors.toList()))
                .build();

        walletEntity.getTransactions().forEach(
                walletTransactionEntity -> walletTransactionEntity.assignWallet(walletEntity)
        );

        return walletEntity;
    }
}
