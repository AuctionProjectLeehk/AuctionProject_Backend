package com.leehk.auction.domain.wallet.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletTransactionJpaRepository extends WalletTransactionRepository, JpaRepository<WalletTransactionEntity, UUID> {
}
