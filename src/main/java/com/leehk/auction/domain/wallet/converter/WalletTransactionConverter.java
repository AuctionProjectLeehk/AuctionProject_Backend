package com.leehk.auction.domain.wallet.converter;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import com.leehk.auction.domain.wallet.infrastructure.WalletEntity;
import com.leehk.auction.domain.wallet.infrastructure.WalletTransactionEntity;

public class WalletTransactionConverter {

    /**
     * WalletTransactionEntity -> WalletTransaction 도메인 변환
     *
     * @param walletTransactionEntity WalletTransactionEntity
     * @return WalletTransaction 도메인 객체
     */
    public static WalletTransaction entityToDomain(WalletTransactionEntity walletTransactionEntity) {
        return WalletTransaction.builder()
                .walletId(walletTransactionEntity.getWallet().getPublicId())
                .money(new Money(walletTransactionEntity.getAmount()))
                .transactionType(walletTransactionEntity.getTransactionType())
                .createdAt(walletTransactionEntity.getCreatedAt())
                .build();
    }

    /**
     * WalletTransaction 도메인 -> WalletTransactionEntity 변환
     *
     * @param walletTransaction WalletTransaction 도메인 객체
     * @param walletEntity      WalletEntity 연관관계
     * @return WalletTransactionEntity
     */
    public static WalletTransactionEntity domainToEntity(WalletTransaction walletTransaction, WalletEntity walletEntity) {
        return WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(walletTransaction.getTransactionType())
                .amount(walletTransaction.getMoney().getAmount())
                .createdAt(walletTransaction.getCreatedAt())
                .build();
    }
}
