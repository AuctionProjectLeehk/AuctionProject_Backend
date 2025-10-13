package com.leehk.auction.domain.wallet.application;

import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import com.leehk.auction.domain.wallet.enums.TransactionType;

import java.util.List;
import java.util.UUID;

public interface WalletTransactionService {

    WalletTransaction deposit(UUID walletId, long amount);

    WalletTransaction withdraw(UUID walletId, long amount);

    List<WalletTransaction> getTransactions(UUID walletId);

    WalletTransaction getTransactionById(UUID transactionId);
}
