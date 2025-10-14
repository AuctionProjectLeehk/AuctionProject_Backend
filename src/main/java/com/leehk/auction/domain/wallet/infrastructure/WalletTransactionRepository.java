package com.leehk.auction.domain.wallet.infrastructure;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletTransactionRepository {

    /**
     * 입력된 거래 내역을 저장너가
     * 
     * @param walletTransactionEntity 거래 내역 엔티티
     * @return WalletTransactionEntity
     */
    WalletTransactionEntity save(WalletTransactionEntity walletTransactionEntity);

    /**
     * 특정 거래 내역 조회
     *
     * @param WalletTransactionId 거래 내역 Id
     * @return Optional<WalletTransactionEntity>
     */
    Optional<WalletTransactionEntity> findById(UUID WalletTransactionId);

    /**
     * 특정 지갑의 모든 거래 내역 조회
     *
     * @param wallet WalletEntity
     * @return List<WalletTransactionEntity>
     */
    List<WalletTransactionEntity> findByWallet(WalletEntity wallet);

    /**
     * 특정 지갑의 거래 내역 최신 순 조회
     *
     * @param wallet WalletEntity
     * @return List<WalletTransactionEntity>
     */
    List<WalletTransactionEntity> findByWalletOrderByCreatedAtDesc(WalletEntity wallet);
}
