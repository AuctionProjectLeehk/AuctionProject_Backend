package com.leehk.auction.domain.wallet.infrastructure;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.wallet.domain.Wallet;
import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
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
        Map<UUID, WalletTransactionEntity> existingWalletTransactionsById = this.transactions.stream()
                .filter(walletTransactionEntity -> walletTransactionEntity.getId() != null)
                .collect(Collectors.toMap(WalletTransactionEntity::getId, Function.identity()));

        List<WalletTransactionEntity> updateWalletTransactionEntities = new ArrayList<>();
        for (WalletTransaction tx: wallet.getTransactions()) {
            UUID txId = tx.getId();
            if (txId != null && existingWalletTransactionsById.containsKey(txId)) {
                // 기존 영속 객체 업데이트
                WalletTransactionEntity exist = existingWalletTransactionsById.remove(txId);
                exist.updateFromDomain(tx);
                updateWalletTransactionEntities.add(exist);
            } else {
                WalletTransactionEntity walletTransactionEntity = WalletTransactionEntity.builder()
                        .wallet(this)
                        .transactionType(tx.getTransactionType())
                        .amount(tx.getMoney().getAmount())
                        .build();
                updateWalletTransactionEntities.add(walletTransactionEntity);
            }
        }

        // ✅ 도메인에서 제거된 트랜잭션 제거 (orphanRemoval = true 로 DB에서도 삭제됨)
        for (WalletTransactionEntity removed: existingWalletTransactionsById.values())
            this.transactions.remove(removed);

        // ✅ 기존 리스트 갱신 (clear() 하면 detach 되므로 addAll로만 갱신)
        this.transactions.retainAll(updateWalletTransactionEntities);
        for (WalletTransactionEntity tx : updateWalletTransactionEntities) {
            if (!this.transactions.contains(tx)) {
                this.transactions.add(tx);
            }
        }
    }
}
