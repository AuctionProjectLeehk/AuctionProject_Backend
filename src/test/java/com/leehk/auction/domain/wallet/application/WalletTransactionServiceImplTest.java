package com.leehk.auction.domain.wallet.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.wallet.domain.Wallet;
import com.leehk.auction.domain.wallet.domain.WalletTransaction;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class WalletTransactionServiceImplTest extends BaseH2Test {

    @Autowired
    private WalletTransactionService walletTransactionService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    private User makeUser(long index) {
        return User.builder()
                .email("test" + index + "@example.com")
                .name("test" + index)
                .password("password")
                .nickname("test" + index)
                .build();
    }

    @Test
    @DisplayName("지갑에 저장된 모든 거래 내역 찾기 - 성공")
    void getTransactions_Success() {
        // given
        User savedUser = userService.saveUser(makeUser(3L));
        Wallet createdwallet = walletService.createWallet(savedUser.getId(), "wallet1");
        UUID walletId = createdwallet.getPublicId();

        long amount1 = 1000L;
        walletService.deposit(walletId, amount1);

        long amount2 = 1200L;
        walletService.deposit(walletId, amount2);

        long amount3 = 800L;
        walletService.withdraw(walletId, amount3);

        // when
        List<WalletTransaction> savedWalletTransactions = walletTransactionService.getTransactions(walletId);
        Wallet updatedWallet = walletService.getWalletById(walletId);
        for (WalletTransaction tx: updatedWallet.getTransactions())
            System.out.println("hi " + tx.getMoney().getAmount() + " " + tx.getWalletId());

        // then
        assertThat(savedWalletTransactions.size()).isEqualTo(3);
        assertThat(savedWalletTransactions.get(0).getMoney().getAmount()).isEqualTo(amount1);
        assertThat(savedWalletTransactions.get(1).getMoney().getAmount()).isEqualTo(amount2);
        assertThat(savedWalletTransactions.get(2).getMoney().getAmount()).isEqualTo(amount3);

        assertThat(updatedWallet.getMoney().getAmount()).isEqualTo(amount1+amount2-amount3);
    }
}