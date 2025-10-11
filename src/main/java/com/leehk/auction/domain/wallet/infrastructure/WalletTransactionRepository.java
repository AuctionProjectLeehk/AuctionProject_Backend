package com.leehk.auction.domain.wallet.infrastructure;


import java.util.List;

public interface WalletTransactionRepository {

    /**
     * 입력된 거래 내역을 저장너가
     * 
     * @param walletTransactionEntity 거래 내역 엔티티
     * @return Wa;;etTransactionEntity
     */
    WalletTransactionEntity save(WalletTransactionEntity walletTransactionEntity);

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
    List<WalletTransactionEntity> findByWalletOrderByCreateAtDesc(WalletEntity wallet);
}
