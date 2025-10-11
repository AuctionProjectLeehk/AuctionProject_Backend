package com.leehk.auction.domain.wallet.infrastructure;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import scala.None;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    private WalletEntity makeWalletEntity(long index) {
        return WalletEntity.builder()
                .publicId(UUID.randomUUID())
                .walletName("testWallet" + index)
                .userId(index)
                .money(new Money(10000L))
                .walletStatus(WalletStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("지갑 저장 - 성공")
    void saveWallet_Success() {
        // given
        WalletEntity walletEntity = makeWalletEntity(1L);

        // when
        WalletEntity savedWallet = walletRepository.save(walletEntity);

        // then
        assertThat(savedWallet).isNotNull();
        assertThat(savedWallet.getPublicId()).isEqualTo(walletEntity.getPublicId());
    }

    @Test
    @DisplayName("유저 Id로 지갑 찾기 - 성공")
    void findByUserId_Success() {
        // given
        Long userId = 1L;
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(userId));

        // when
        WalletEntity foundWalletEntity = walletRepository.findByUserId(userId)
                .orElse(null);

        // then
        assertThat(walletEntity).isNotNull();
        assertThat(foundWalletEntity.getUserId()).isEqualTo(userId);
        assertThat(walletEntity.getPublicId()).isEqualTo(foundWalletEntity.getPublicId());
    }
    
    @Test
    @DisplayName("유저 Id로 지갑 찾기 - 실패: 없는 유저 참조")
    void findByUserId_Fail_NotFoundUser() {
        // given
        Long userId = 1L;
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(userId));

        // when
        WalletEntity foundWalletEntity = walletRepository.findByUserId(150L)
                .orElse(null);

        // then
        assertThat(foundWalletEntity).isNull();
    }
    
    @Test
    @DisplayName("유저 Id와 지갑 이름으로 지갑 찾기 - 성공")
    void findByUserIdAndWalletName_Success() {
        // given
        Long userId = 1L;
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(userId));
        String walletName = walletEntity.getWalletName();

        // given
        WalletEntity foundWalletEntity = walletRepository.findByUserIdAndWalletName(userId, walletName)
                .orElse(null);

        // then
        assertThat(walletEntity).isNotNull();
        assertThat(foundWalletEntity.getUserId()).isEqualTo(userId);
        assertThat(walletEntity.getPublicId()).isEqualTo(foundWalletEntity.getPublicId());
    }

    @Test
    @DisplayName("유저 Id와 지갑 이름으로 지갑 찾기 - 실패")
    void findByUserIdAndWalletName_Fail_Exception() {
        // given
        Long userId1 = 1L;
        WalletEntity walletEntity1 = walletRepository.save(makeWalletEntity(userId1));
        String walletName1 = walletEntity1.getWalletName();

        Long userId2 = 2L;
        WalletEntity walletEntity2 = walletRepository.save(makeWalletEntity(userId2));
        String walletName2 = walletEntity2.getWalletName();

        // when and then
        WalletEntity foundWalletEntity1 = walletRepository.findByUserIdAndWalletName(1L, walletName2)
                .orElse(null);

        WalletEntity foundWalletEntity2 = walletRepository.findByUserIdAndWalletName(2L, walletName1)
                .orElse(null);

        assertThat(foundWalletEntity1).isNull();
        assertThat(foundWalletEntity2).isNull();
    }
}