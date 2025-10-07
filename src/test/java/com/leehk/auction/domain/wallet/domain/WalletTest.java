package com.leehk.auction.domain.wallet.domain;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    private User testUser;
    private Wallet testWallet;

    private User makeUser(long index) {
        return User.builder()
                .email("test" + index + "@example.com")
                .name("test" + index)
                .password("password")
                .nickname("test" + index)
                .build();
    }

    private Wallet makeWallet(long index, Long userId) {
        return Wallet.builder()
                .userId(userId)
                .wallName("walletName" + index)
                .build();
    }
    
    @Test
    @DisplayName("지갑 생성 - 성공")
    void createWallet_Success() {
        // given
        User user = makeUser(1L);

        // when
        Wallet wallet = makeWallet(2L, user.getId());

        // then
        assertThat(wallet.getUserId()).isEqualTo(user.getId());
        assertThat(wallet.getWalletName()).isEqualTo("walletName" + 2L);
        assertThat(wallet.getMoney().getAmount()).isEqualTo(0L);
        assertThat(wallet.getWalletStatus()).isEqualTo(WalletStatus.ACTIVE);
        assertThat(wallet.getTransactions()).isEmpty();
    }

    @Test
    @DisplayName("지갑 이름 업데이트 - 성공")
    void updatedWalletName_Success() {
        // given
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());

        // when
        Wallet updatedWallet = wallet.updatedWalletName("newWalletName");

        // then
        assertThat(updatedWallet.getWalletName()).isEqualTo("newWalletName");
    }

    @Test
    @DisplayName("지갑 이름 업데이트 - 실패")
    void updatedWallName_Fail_InvalidWalletName() {
        // given
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());

        // when and then
        assertThatThrownBy(() -> wallet.updatedWalletName(null))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_WALLET_NAME.getMessage());

        assertThatThrownBy(() -> wallet.updatedWalletName(""))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_WALLET_NAME.getMessage());

        assertThatThrownBy(() -> wallet.updatedWalletName(wallet.getWalletName()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_WALLET_NAME.getMessage());
    }
    
    @Test
    @DisplayName("지갑 활성화 - 성공")
    void activate_Success() {
        // given
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());

        // when
        wallet.activate();

        // then
        assertThat(wallet.getWalletStatus()).isEqualTo(WalletStatus.ACTIVE);
    }

    @Test
    @DisplayName("지갑 일시정지 - 성공")
    void suspend_Success() {
        // given
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());

        // when
        wallet.suspend();

        // then
        assertThat(wallet.getWalletStatus()).isEqualTo(WalletStatus.SUSPENDED);
    }

    @Test
    @DisplayName("지갑 종료 - 성공")
    void close_Success() {
        // given
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());

        // when
        wallet.close();

        // then
        assertThat(wallet.getWalletStatus()).isEqualTo(WalletStatus.CLOSED);
    }

    @Test
    @DisplayName("지갑에 입금 - 성공")
    void deposit_Success() {
        // given
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());

        // when
        Wallet depositedWallet = wallet.deposit(new Money(3000L));

        // then
        assertThat(wallet.getMoney().getAmount()).isEqualTo(3000L);
        assertThat(depositedWallet.getMoney().getAmount()).isEqualTo(3000L);
        assertThat(wallet == depositedWallet).isTrue();
    }
    
    @Test
    @DisplayName("지갑에서 출금 - 성공")
    void withdraw_Success() {
        // given
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());
        Wallet depositedWallet = wallet.deposit(new Money(3000L));

        // when
        Wallet withdrawedWallet = wallet.withdraw(new Money(2000L));

        // then
        assertThat(wallet.getMoney().getAmount()).isEqualTo(1000L);
        assertThat(depositedWallet.getMoney().getAmount()).isEqualTo(1000L);
        assertThat(withdrawedWallet.getMoney().getAmount()).isEqualTo(1000L);
        assertThat(wallet == withdrawedWallet).isTrue();
    }

    @Test
    @DisplayName("지갑에서 출금 - 실패: 너무 많은 출금")
    void withdraw_Fail_TooManyDeposit() {
        // given
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());
        Wallet depositedWallet = wallet.deposit(new Money(3000L));

        // when and then
        assertThatThrownBy(() -> depositedWallet.withdraw(new Money(5000L)))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AMOUNT_TOO_MANY.getMessage());
    }

    @Test
    @DisplayName("금액 송금 - 성공")
    void transfer_Success() {
        // given
        User user1 = makeUser(1L);
        Wallet wallet1 = makeWallet(11L, user1.getId());
        wallet1.deposit(new Money(10000L));

        User user2 = makeUser(2L);
        Wallet wallet2 = makeWallet(22L, user2.getId());
        wallet2.deposit(new Money(12000L));

        // when
        wallet1.transfer(wallet2, new Money(3000L));

        // then
        assertThat(wallet1.getMoney().getAmount()).isEqualTo(7000L);
        assertThat(wallet2.getMoney().getAmount()).isEqualTo(15000L);
    }
    
    @Test
    @DisplayName("지갑에서 다른 지갑으로 금액 송금 - 실패: 너무 많은 금액 송금")
    void transfer_Fail_TooManyTransfer() {
        User user1 = makeUser(1L);
        Wallet wallet1 = makeWallet(11L, user1.getId());
        wallet1.deposit(new Money(10000L));

        User user2 = makeUser(2L);
        Wallet wallet2 = makeWallet(22L, user2.getId());
        wallet2.deposit(new Money(12000L));

        // when
        assertThatThrownBy(() -> wallet1.transfer(wallet2, new Money(11000L)))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AMOUNT_TOO_MANY.getMessage());
    }

    @Test
    @DisplayName("지갑 잔액 확인 - 성공")
    void hasEnoughBalance_Success() {
        // then
        User user = makeUser(1L);
        Wallet wallet = makeWallet(2L, user.getId());
        wallet.deposit(new Money(10000L));

        // when and then
        assertThat(wallet.hasEnoughBalance(7000L)).isTrue();
        assertThat(wallet.hasEnoughBalance(10000L)).isTrue();
        assertThat(wallet.hasEnoughBalance(12000L)).isFalse();
    }
}