package com.leehk.auction.domain.wallet.infrastructure;

import java.util.Optional;

public interface WalletRepository {

    /**
     * 입력받은 지갑 저장
     *
     * @param wallet Wallet
     * @return WalletEntity
     */
    WalletEntity save(WalletEntity wallet);

    /**
     * 사용자 ID로 지갑 조회
     *
     * @param userId 사용자 ID
     * @return Optional<WalletEntity>
     */
    Optional<WalletEntity> findByUserId(Long userId);

    /**
     * 사용자 ID + 지갑 이름으로 조회 (중복 방지 등)
     *
     * @param userId     사용자 ID
     * @param walletName 지갑 이름
     * @return Optional<WalletEntity>
     */
    Optional<WalletEntity> findByUserIdAndWalletName(Long userId, String walletName);
}
