package com.leehk.auction.domain.wallet.infrastructure;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.user.infrastructure.UserEntity;
import com.leehk.auction.domain.user.infrastructure.UserRepository;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testUserEntity = userRepository.save(UserEntity.builder()
                        .publicId(UUID.randomUUID())
                        .email("random@test.com")
                        .name("testUser")
                        .password("testPassword")
                        .nickname("testUserNickname")
                .build());
    }

    private WalletEntity makeWalletEntity(long index) {
        return WalletEntity.builder()
                .publicId(UUID.randomUUID())
                .userEntity(testUserEntity)
                .walletName("testWallet" + index)
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
    @DisplayName("Id로 지갑 찾기 - 성공")
    void findById_Success() {
        // given
        Long userId = 1L;
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(userId));
        UUID walletId = walletEntity.getPublicId();

        // when
        WalletEntity foundWalletEntity = walletRepository.findById(walletId)
                .orElse(null);

        // then
        assertThat(foundWalletEntity).isNotNull();
        assertThat(walletEntity.getUserEntity()).isEqualTo(foundWalletEntity.getUserEntity());
        assertThat(walletEntity.getPublicId()).isEqualTo(foundWalletEntity.getPublicId());
    }
    
    @Test
    @DisplayName("Id로 지갑 찾기 - 실패: 없는 지갑 Id로 참조")
    void findById_Fail_NotFoundWallet() {
        // given
        Long userId = 1L;
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(userId));

        // when
        WalletEntity foundWalletEntity = walletRepository.findById(UUID.randomUUID())
                .orElse(null);

        // then
        assertThat(foundWalletEntity).isNull();
    }

    @Test
    @DisplayName("유저 Id로 지갑 찾기 - 성공")
    void findByUserEntityId_Success() {
        // given
        Long userId = testUserEntity.getId();
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(userId));

        // when
        WalletEntity foundWalletEntity = walletRepository.findByUserEntity_Id(userId)
                .orElse(null);

        // then
        assertThat(walletEntity).isNotNull();
        assertThat(foundWalletEntity.getUserEntity().getId()).isEqualTo(userId);
        assertThat(walletEntity.getPublicId()).isEqualTo(foundWalletEntity.getPublicId());
    }
    
    @Test
    @DisplayName("유저 Id로 지갑 찾기 - 실패: 없는 유저 참조")
    void findByUserId_Fail_NotFoundUserEntity() {
        // given
        Long userId = 1L;
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(userId));

        // when
        WalletEntity foundWalletEntity = walletRepository.findByUserEntity_Id(150L)
                .orElse(null);

        // then
        assertThat(foundWalletEntity).isNull();
    }
    
    @Test
    @DisplayName("유저 Id와 지갑 이름으로 지갑 찾기 - 성공")
    void findByUserEntityIdAndWalletName_Success() {
        // given
        Long userId = testUserEntity.getId();
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(userId));
        String walletName = walletEntity.getWalletName();

        // given
        WalletEntity foundWalletEntity = walletRepository.findByUserEntity_IdAndWalletName(userId, walletName)
                .orElse(null);

        // then
        assertThat(walletEntity).isNotNull();
        assertThat(foundWalletEntity.getUserEntity().getId()).isEqualTo(userId);
        assertThat(walletEntity.getPublicId()).isEqualTo(foundWalletEntity.getPublicId());
    }

    @Test
    @DisplayName("유저 Id와 지갑 이름으로 지갑 찾기 - 실패")
    void findByUserEntityIdAndWalletName_Fail_Exception() {
        // given
        Long userId1 = 1L;
        WalletEntity walletEntity1 = walletRepository.save(makeWalletEntity(userId1));
        String walletName1 = walletEntity1.getWalletName();

        Long userId2 = 2L;
        WalletEntity walletEntity2 = walletRepository.save(makeWalletEntity(userId2));
        String walletName2 = walletEntity2.getWalletName();

        // when and then
        WalletEntity foundWalletEntity1 = walletRepository.findByUserEntity_IdAndWalletName(1L, walletName2)
                .orElse(null);

        WalletEntity foundWalletEntity2 = walletRepository.findByUserEntity_IdAndWalletName(2L, walletName1)
                .orElse(null);

        assertThat(foundWalletEntity1).isNull();
        assertThat(foundWalletEntity2).isNull();
    }
}