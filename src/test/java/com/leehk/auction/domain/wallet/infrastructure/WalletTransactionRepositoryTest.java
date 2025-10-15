package com.leehk.auction.domain.wallet.infrastructure;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.user.infrastructure.UserEntity;
import com.leehk.auction.domain.user.infrastructure.UserRepository;
import com.leehk.auction.domain.wallet.enums.TransactionType;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WalletTransactionRepositoryTest {

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

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
    @DisplayName("거래 내역 저장 - 성공")
    void saveWalletTransaction_Success() {
        // given
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(1L));

        // when
        WalletTransactionEntity transaction1 = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.DEPOSIT)
                .amount(1000L)
                .build();

        WalletTransactionEntity savedTransaction = walletTransactionRepository.save(transaction1);

        // then
        assertThat(savedTransaction).isNotNull();
        assertThat(savedTransaction.getId()).isEqualTo(transaction1.getId());
        assertThat(savedTransaction.getWallet()).isEqualTo(walletEntity);
    }

    @Test
    @DisplayName("거래 내역 ID로 찾기 - 성공")
    void findById_Success() {
        // given
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(1L));

        WalletTransactionEntity savedTransaction = walletTransactionRepository.save(WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.DEPOSIT)
                .amount(1000L)
                .build()
        );

        walletRepository.save(walletEntity);

        // when
        WalletTransactionEntity foundWalletTransactionEntity = walletTransactionRepository.findById(savedTransaction.getId())
                .orElse(null);

        // then
        assertThat(foundWalletTransactionEntity).isNotNull();
        assertThat(foundWalletTransactionEntity.getWallet()).isEqualTo(savedTransaction.getWallet());
        assertThat(foundWalletTransactionEntity.getTransactionType()).isEqualTo(savedTransaction.getTransactionType());
    }

    @Test
    @DisplayName("거래 내역 ID로 찾기 - 실패: 없는 Id로 조회")
    void findById_Fail_NotFoundWalletTransaction() {
        // given
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(1L));

        WalletTransactionEntity transaction = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.DEPOSIT)
                .amount(1000L)
                .build();

        walletRepository.save(walletEntity);
        walletTransactionRepository.save(transaction);

        // when
        WalletTransactionEntity foundWalletTransactionEntity = walletTransactionRepository.findById(UUID.randomUUID())
                .orElse(null);

        // then
        assertThat(foundWalletTransactionEntity).isNull();
    }
    
    @Test
    @DisplayName("지갑에 저장된 모든 거래 내역 찾기 - 성공")
    void findByWallet_Success() {
        // given
        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(1L));

        WalletTransactionEntity transaction1 = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.DEPOSIT)
                .amount(1000L)
                .build();
        WalletTransactionEntity transaction2 = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.TRANSFER)
                .amount(100L)
                .build();
        WalletTransactionEntity transaction3 = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.WITHDRAW)
                .amount(200L)
                .build();

        walletRepository.save(walletEntity);
        walletTransactionRepository.save(transaction1);
        walletTransactionRepository.save(transaction2);
        walletTransactionRepository.save(transaction3);

        // when
        List<WalletTransactionEntity> transactions = walletTransactionRepository.findByWallet(walletEntity);

        // then
        assertThat(transactions.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("지갑에 저장된 모든 거래 내역을 생성 날짜 순으로 찾기 - 성공")
    void findByWalletOrderByCreatedAtDesc_Success() {
        // given
        LocalDateTime now = LocalDateTime.now();

        WalletEntity walletEntity = walletRepository.save(makeWalletEntity(1L));

        WalletTransactionEntity transaction1 = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.DEPOSIT)
                .amount(1000L)
                .createdAt(now.plusDays(20))
                .build();
        WalletTransactionEntity transaction2 = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.TRANSFER)
                .createdAt(now.plusDays(10))
                .amount(100L)
                .build();
        WalletTransactionEntity transaction3 = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .transactionType(TransactionType.WITHDRAW)
                .amount(200L)
                .createdAt(now.plusDays(15))
                .build();

        walletRepository.save(walletEntity);
        walletTransactionRepository.save(transaction1);
        walletTransactionRepository.save(transaction2);
        walletTransactionRepository.save(transaction3);

        // when
        List<WalletTransactionEntity> orderedTransactions = walletTransactionRepository.findByWalletOrderByCreatedAtDesc(walletEntity);

        // then: 1 3 2 순서 확인
        assertThat(orderedTransactions.size()).isEqualTo(3);
        assertThat(orderedTransactions.stream()
                .map(WalletTransactionEntity::getCreatedAt)
                .toArray()).isEqualTo(new LocalDateTime[]{now.plusDays(20), now.plusDays(15), now.plusDays(10)});
        assertThat(orderedTransactions.stream()
                .map(WalletTransactionEntity::getId)
                .toArray()).isEqualTo(new UUID[]{transaction1.getId(), transaction3.getId(), transaction2.getId()});
    }

}