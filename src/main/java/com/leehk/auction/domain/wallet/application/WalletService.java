package com.leehk.auction.domain.wallet.application;

import com.leehk.auction.domain.wallet.domain.Wallet;
import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import com.leehk.auction.domain.wallet.enums.TransactionType;
import com.leehk.auction.domain.wallet.enums.WalletStatus;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    Wallet createWallet(Long userId, String walletName);

    Wallet getWalletByUserId(Long userId);

    Wallet getWalletById(UUID walletId);

    Wallet changeWalletStatus(UUID walletId, WalletStatus walletStatus);

    Wallet deposit(UUID walletId, long amount);

    Wallet withdraw(UUID walletId, long amount);

    void transfer(UUID fromWalletId, UUID toWalletId, long amount);
}
