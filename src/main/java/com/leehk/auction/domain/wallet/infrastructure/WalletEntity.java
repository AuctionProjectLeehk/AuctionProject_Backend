package com.leehk.auction.domain.wallet.infrastructure;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.wallet.domain.Wallet;
import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
import com.leehk.auction.global.CollectionSyncHelper;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Table(name = "wallets")
@Getter
@NoArgsConstructor
public class WalletEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID publicId;

    private String walletName;

    private Long userId;

    @Embedded
    private Money money;

    private WalletStatus walletStatus;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<WalletTransactionEntity> transactions;

    @Builder
    public WalletEntity(UUID publicId, String walletName, Long userId,
                        Money money, WalletStatus walletStatus, LocalDateTime createdAt,
                        List<WalletTransactionEntity> transactions) {
        this.publicId = publicId != null ? publicId : UUID.randomUUID();
        this.walletName = walletName;
        this.userId = userId;
        this.money = money != null ? money : new Money(0L);
        this.walletStatus = walletStatus != null ? walletStatus : WalletStatus.ACTIVE;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }
    
    public void updateFromDomain(Wallet wallet) {
        this.publicId = wallet.getPublicId();
        this.walletName = wallet.getWalletName();
        this.userId = wallet.getUserId();
        this.money = wallet.getMoney();
        this.walletStatus = wallet.getWalletStatus();
        this.createdAt = wallet.getCreatedAt();

        // transactions 동기화
        CollectionSyncHelper.sync(
                this.transactions,                         // 엔티티 리스트 (영속 상태)
                wallet.getTransactions(),                  // 도메인 리스트
                WalletTransaction::getId,                  // 도메인 ID 추출
                WalletTransactionEntity::getId,            // 엔티티 ID 추출
                WalletTransactionEntity::updateFromDomain,  // 업데이트 함수
                domain -> WalletTransactionEntity.builder()            // 새 엔티티 생성 함수
                        .wallet(this)
                        .transactionType(domain.getTransactionType())
                        .amount(domain.getMoney().getAmount())
                        .build()
        );
    }
}
