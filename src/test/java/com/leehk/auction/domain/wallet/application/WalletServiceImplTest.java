package com.leehk.auction.domain.wallet.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.wallet.domain.Wallet;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class WalletServiceImplTest extends BaseH2Test {

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

    private Wallet makeWallet(Long userId, long index) {
        return Wallet.builder()
                .userId(userId)
                .walletName("walletName" + index)
                .build();
    }

    @Test
    @DisplayName("지갑 생성 - 성공")
    void createWallet_Success() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));

        // when
        Wallet createdwallet = walletService.createWallet(savedUser.getId(), "wallet1");

        // then
        assertThat(createdwallet).isNotNull();
        assertThat(createdwallet.getUserId()).isEqualTo(savedUser.getId());
    }
    
    @Test
    @DisplayName("지갑 생성 - 실퍠: 존재하는 지갑 생성")
    void createWallet_Fail_WalletAlreadyExist() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        // when and then
        assertThatThrownBy(() -> walletService.createWallet(savedUser.getId(), "wallet1"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_ALREADY_EXIST.getMessage());
    }

    @Test
    @DisplayName("User Id로 지갑 찾기 - 성공")
    void getWalletByUserId_Success() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        // when
        Wallet foundWallet = walletService.getWalletByUserId(createdWallet.getUserId());

        // then
        assertThat(foundWallet).isNotNull();
        assertThat(foundWallet.getPublicId()).isEqualTo(createdWallet.getPublicId());
        assertThat(foundWallet.getWalletName()).isEqualTo(createdWallet.getWalletName());
    }

    @Test
    @DisplayName("User Id로 지갑 찾기 - 실퍠: 해당 유저의 지갑을 찾지 못함")
    void getWalletByUserId_Fail_NotFoundWallet() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        // when and then
        assertThatThrownBy(() -> walletService.getWalletByUserId(2L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("id로 지갑 찾기 - 성공")
    void getWalletById_Success() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        // when
        Wallet foundWallet = walletService.getWalletById(createdWallet.getPublicId());

        // then
        assertThat(foundWallet).isNotNull();
        assertThat(foundWallet.getUserId()).isEqualTo(createdWallet.getUserId());
        assertThat(foundWallet.getWalletName()).isEqualTo(createdWallet.getWalletName());
    }

    @Test
    @DisplayName("id로 지갑 찾기 - 실패: 해당 지갑을 찾지 못함")
    void getWalletById_Fail_NotFoundWallet() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        // when and then
        assertThatThrownBy(() -> walletService.getWalletById(UUID.randomUUID()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("지갑 상태 변경 - 성공")
    void changeWalletStatus_Success() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        // when and then
        assertThat(createdWallet.getWalletStatus()).isEqualTo(WalletStatus.ACTIVE);

        Wallet suspendedWallet = walletService.changeWalletStatus(createdWallet.getPublicId(), WalletStatus.SUSPENDED);
        assertThat(suspendedWallet.getWalletStatus()).isEqualTo(WalletStatus.SUSPENDED);

        Wallet closedWallet = walletService.changeWalletStatus(createdWallet.getPublicId(), WalletStatus.CLOSED);
        assertThat(closedWallet.getWalletStatus()).isEqualTo(WalletStatus.CLOSED);
    }

    @Test
    @DisplayName("지갑 상태 변경 - 실패: 해당 지갑을 찾지 못함")
    void changeWalletStatus_Fail_NotFoundWallet() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        // when and then
        assertThatThrownBy(() -> walletService.changeWalletStatus(UUID.randomUUID(), WalletStatus.ACTIVE))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> walletService.changeWalletStatus(UUID.randomUUID(), WalletStatus.SUSPENDED))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> walletService.changeWalletStatus(UUID.randomUUID(), WalletStatus.CLOSED))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());
    }
    
    @Test
    @DisplayName("입금하기 - 성공")
    void deposit_Success() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");
        long amount = 1000L;

        // when
        Wallet depositedWallet = walletService.deposit(createdWallet.getPublicId(), amount);

        // then
        assertThat(depositedWallet).isNotNull();
        assertThat(depositedWallet.getMoney().getAmount()).isEqualTo(amount);
    }

    @Test
    @DisplayName("입금하기 - 해당 지갑을 찾지 못함")
    void deposit_Fail_NotFoundWallet() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");
        long amount = 1000L;
        
        // when and then
        assertThatThrownBy(() -> walletService.deposit(UUID.randomUUID(), amount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());
    }
    
    @Test
    @DisplayName("출금하기 - 성공")
    void withdraw_Success() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");
        
        long depositedAmount = 1000L;
        Wallet depositedWallet = walletService.deposit(createdWallet.getPublicId(), depositedAmount);
        
        // when
        long withdrawAmount = 500L;
        Wallet withdrawWallet = walletService.withdraw(createdWallet.getPublicId(), withdrawAmount);

        // then
        assertThat(withdrawWallet).isNotNull();
        assertThat(withdrawWallet.getMoney().getAmount()).isEqualTo(depositedAmount - withdrawAmount);
    }

    @Test
    @DisplayName("출금하기 - 실패: 해당 지갑을 찾지 못함")
    void withdraw_Fail_NotFoundWallet() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        long depositedAmount = 1000L;
        Wallet depositedWallet = walletService.deposit(createdWallet.getPublicId(), depositedAmount);

        long withdrawAmount = 500L;

        // when and then
        assertThatThrownBy(() -> walletService.deposit(UUID.randomUUID(), withdrawAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("출금하기 - 실패: 잔액보다 많이 출금")
    void withdraw_Fail_TooManyWithdraw() {
        // given
        User savedUser = userService.saveUser(makeUser(1L));
        Wallet createdWallet = walletService.createWallet(savedUser.getId(), "wallet1");

        long depositedAmount = 1000L;
        Wallet depositedWallet = walletService.deposit(createdWallet.getPublicId(), depositedAmount);

        long withdrawAmount = 1500L;

        // when and then
        assertThatThrownBy(() -> walletService.withdraw(createdWallet.getPublicId(), withdrawAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AMOUNT_TOO_MANY.getMessage());
    }

    @Test
    @DisplayName("계좌 이체 - 성공")
    void transfer_Success() {
        // given
        User savedUser1 = userService.saveUser(makeUser(1L));
        Wallet createdWallet1 = walletService.createWallet(savedUser1.getId(), "wallet1");

        User savedUser2 = userService.saveUser(makeUser(2L));
        Wallet createdWallet2 = walletService.createWallet(savedUser2.getId(), "wallet2");

        long depositedAmount = 1000L;
        Wallet depositedWallet = walletService.deposit(createdWallet1.getPublicId(), depositedAmount);

        long transferAmount = 900L;

        // when
        walletService.transfer(createdWallet1.getPublicId(), createdWallet2.getPublicId(), transferAmount);
        Wallet updatedWallet1 = walletService.getWalletById(createdWallet1.getPublicId());
        Wallet updatedWallet2 = walletService.getWalletById(createdWallet2.getPublicId());

        // then
        assertThat(updatedWallet1).isNotNull();
        assertThat(updatedWallet1.getMoney().getAmount()).isEqualTo(depositedAmount - transferAmount);

        assertThat(updatedWallet2).isNotNull();
        assertThat(updatedWallet2.getMoney().getAmount()).isEqualTo(transferAmount);
    }

    @Test
    @DisplayName("계좌 이체 - 실패: 해당 지갑을 찾지 못함")
    void transfer_Fail_NotFoundWallet() {
        // given
        User savedUser1 = userService.saveUser(makeUser(1L));
        Wallet createdWallet1 = walletService.createWallet(savedUser1.getId(), "wallet1");

        User savedUser2 = userService.saveUser(makeUser(2L));
        Wallet createdWallet2 = walletService.createWallet(savedUser2.getId(), "wallet2");

        long depositedAmount = 1000L;
        Wallet depositedWallet = walletService.deposit(createdWallet1.getPublicId(), depositedAmount);

        long transferAmount = 900L;

        // when and then
        assertThatThrownBy(() -> walletService.transfer(createdWallet1.getPublicId(), UUID.randomUUID(), transferAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> walletService.transfer(UUID.randomUUID(), createdWallet1.getPublicId(), transferAmount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.WALLET_NOT_FOUND.getMessage());
    }
}
