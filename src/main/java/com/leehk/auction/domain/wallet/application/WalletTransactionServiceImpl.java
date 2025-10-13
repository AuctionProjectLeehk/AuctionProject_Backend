package com.leehk.auction.domain.wallet.application;

import com.leehk.auction.domain.wallet.converter.WalletTransactionConverter;
import com.leehk.auction.domain.wallet.domain.Wallet;
import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import com.leehk.auction.domain.wallet.infrastructure.WalletEntity;
import com.leehk.auction.domain.wallet.infrastructure.WalletRepository;
import com.leehk.auction.domain.wallet.infrastructure.WalletTransactionEntity;
import com.leehk.auction.domain.wallet.infrastructure.WalletTransactionRepository;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletService walletService;

    @Override
    @Transactional
    public WalletTransaction deposit(UUID walletId, long amount) {
        Wallet wallet = walletService.deposit(walletId, amount);

        return wallet.getTransactions().get(wallet.getTransactions().size()-1);
    }

    @Override
    @Transactional
    public WalletTransaction withdraw(UUID walletId, long amount) {
        Wallet wallet = walletService.withdraw(walletId, amount);

        return wallet.getTransactions().get(wallet.getTransactions().size()-1);
    }

    @Override
    @Transactional
    public List<WalletTransaction> getTransactions(UUID walletId) {
        WalletEntity savedWalletEntity = walletRepository.findById(walletId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        return walletTransactionRepository.findByWallet(savedWalletEntity)
                .stream()
                .map(WalletTransactionConverter::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public WalletTransaction getTransactionById(UUID transactionId) {
        WalletTransactionEntity walletTransactionEntity = walletTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_TRANSACTION_NOT_FOUND));

        return WalletTransactionConverter.entityToDomain(walletTransactionEntity);
    }
}
