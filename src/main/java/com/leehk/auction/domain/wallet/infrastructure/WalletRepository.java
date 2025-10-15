package com.leehk.auction.domain.wallet.infrastructure;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {

    /**
     * 입력받은 지갑 저장
     *
     * @param wallet Wallet
     * @return WalletEntity
     */
    WalletEntity save(WalletEntity wallet);

    /**
     * Wallet ID로 지갑 조회
     *
     * @param walletId 지갑 ID
     * @return Optional<WalletEntity>
     */
    Optional<WalletEntity> findById(UUID walletId);

    /**
     * 사용자 ID로 지갑 조회
     *
     * @param userId 사용자 ID
     * @return Optional<WalletEntity>
     */
    Optional<WalletEntity> findByUserEntity_Id(Long userId);

    /**
     * 사용자 ID + 지갑 이름으로 조회 (중복 방지 등)
     *
     * @param userId     사용자 ID
     * @param walletName 지갑 이름
     * @return Optional<WalletEntity>
     */
    Optional<WalletEntity> findByUserEntity_IdAndWalletName(Long userId, String walletName);
}
