package com.leehk.auction.domain.wallet.application;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.wallet.converter.WalletConverter;
import com.leehk.auction.domain.wallet.domain.Wallet;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
import com.leehk.auction.domain.wallet.infrastructure.WalletEntity;
import com.leehk.auction.domain.wallet.infrastructure.WalletRepository;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserService userService;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public Wallet createWallet(Long userId, String walletName) {
        walletRepository.findByUserIdAndWalletName(userId, walletName)
                .ifPresent(w -> { throw new CustomException(ErrorCode.WALLET_ALREADY_EXIST); });

        Wallet wallet = Wallet.createWallet(userId, walletName);

        walletRepository.save(WalletConverter.domainToEntity(wallet));

        return wallet;
    }

    @Override
    public Wallet getWalletByUserId(Long userId) {
        WalletEntity savedWalletEntity = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        return WalletConverter.entityToDomain(savedWalletEntity);
    }

    @Override
    public Wallet getWalletById(UUID walletId) {
        WalletEntity savedWalletEntity = walletRepository.findById(walletId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        return WalletConverter.entityToDomain(savedWalletEntity);
    }

    @Override
    @Transactional
    public Wallet changeWalletStatus(UUID walletId, WalletStatus walletStatus) {
        WalletEntity savedWalletEntity = walletRepository.findById(walletId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        Wallet wallet = WalletConverter.entityToDomain(savedWalletEntity);
        switch (walletStatus) {
            case ACTIVE -> wallet.activate();
            case SUSPENDED -> wallet.suspend();
            case CLOSED -> wallet.close();
        }

        savedWalletEntity.updateFromDomain(wallet);

        return wallet;
    }

    @Override
    @Transactional
    public Wallet deposit(UUID walletId, long amount) {
        WalletEntity savedWalletEntity = walletRepository.findById(walletId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        Wallet wallet = WalletConverter.entityToDomain(savedWalletEntity);

        wallet.deposit(new Money(amount));

        savedWalletEntity.updateFromDomain(wallet);

        return wallet;
    }

    @Override
    @Transactional
    public Wallet withdraw(UUID walletId, long amount) {
        WalletEntity savedWalletEntity = walletRepository.findById(walletId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        Wallet wallet = WalletConverter.entityToDomain(savedWalletEntity);

        wallet.withdraw(new Money(amount));

        savedWalletEntity.updateFromDomain(wallet);

        return wallet;
    }

    @Override
    @Transactional
    public void transfer(UUID fromWalletId, UUID toWalletId, long amount) {
        WalletEntity savedFromWalletEntity = walletRepository.findById(fromWalletId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        WalletEntity savedToWalletEntity = walletRepository.findById(toWalletId)
                .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));

        Wallet fromWallet = WalletConverter.entityToDomain(savedFromWalletEntity);
        Wallet toWallet = WalletConverter.entityToDomain(savedToWalletEntity);

        fromWallet.transfer(toWallet, new Money(amount));

        savedFromWalletEntity.updateFromDomain(fromWallet);
        savedFromWalletEntity.updateFromDomain(toWallet);
    }
}
