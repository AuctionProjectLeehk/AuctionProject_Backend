package com.leehk.auction.domain.wallet.domain;

import com.leehk.auction.domain.money.domain.Money;
import com.leehk.auction.domain.wallet.enums.TransactionType;
import com.leehk.auction.domain.wallet.enums.WalletStatus;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Wallet Aggregate Root
 * <p>
 * 사용자의 지갑을 나타내며, 입금, 출금, 송금, 상태 변경 등의
 * 비즈니스 로직을 캡슐화합니다. 또한 WalletTransaction을 통해
 * 모든 금액 변동 내역을 기록합니다.
 */
@Getter
public class Wallet {

    private final UUID publicId;
    private String walletName;
    private final Long userId;
    private Money money;
    private WalletStatus walletStatus;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<WalletTransaction> transactions;


    /**
     * Wallet 생성자
     *
     * @param publicId   지갑 고유 식별자, null일 경우 UUID 자동 생성
     * @param userId     지갑 소유자 ID
     * @param wallName   지갑 이름
     */
    @Builder
    public Wallet(UUID publicId, Long userId, String wallName) {
        this.publicId = publicId != null ? publicId : UUID.randomUUID();
        this.userId = userId;
        this.walletName = wallName;
        this.money = new Money(0L);
        this.walletStatus = WalletStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.transactions = new ArrayList<>();
    }

    /**
     * 지갑 이름을 업데이트합니다.
     *
     * @param newWalletName 새로운 지갑 이름
     * @return 업데이트된 Wallet 객체
     * @throws CustomException 이름이 null이거나 빈 문자열인 경우 또는 기존 이름과 동일한 경우
     */
    public Wallet updatedWalletName(String newWalletName) {
        if (newWalletName == null || newWalletName.isEmpty() || this.walletName.equals(newWalletName)) {
            throw new CustomException(ErrorCode.INVALID_WALLET_NAME);
        }

        this.walletName = newWalletName;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 지갑 상태를 활성화(ACTIVE)로 변경합니다.
     *
     * @return 상태가 ACTIVE로 변경된 Wallet 객체
     */
    public Wallet activate() {
        this.walletStatus = WalletStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 지갑 상태를 일시정지(SUSPENDED)로 변경합니다.
     *
     * @return 상태가 SUSPENDED로 변경된 Wallet 객체
     */
    public Wallet suspend() {
        this.walletStatus = WalletStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 지갑 상태를 종료(CLOSED)로 변경합니다.
     *
     * @return 상태가 CLOSED로 변경된 Wallet 객체
     */
    public Wallet close() {
        this.walletStatus = WalletStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 지정된 금액을 지갑에 입금합니다.
     *
     * @param money 입금할 금액(Money VO)
     * @return 입금 후 Wallet 객체
     */
    public Wallet deposit(Money money) {
        this.money = this.money.add(money);
        this.transactions.add(WalletTransaction.create(this.publicId, this.money, TransactionType.DEPOSIT));
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 지정된 금액을 지갑에서 출금합니다.
     *
     * @param money 출금할 금액(Money VO)
     * @return 출금 후 Wallet 객체
     */
    public Wallet withdraw(Money money) {
        this.money = this.money.subtract(money);
        this.transactions.add(WalletTransaction.create(this.publicId, this.money, TransactionType.WITHDRAW));
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    /**
     * 지정된 금액을 다른 Wallet으로 송금합니다.
     *
     * @param targetWallet 송금 대상 Wallet 객체
     * @param money        송금할 금액(Money VO)
     * @throws CustomException 잔액 부족, 상태 불가 등 비즈니스 규칙 위반 시
     */
    public void transfer(Wallet targetWallet, Money money) {
        this.withdraw(money);
        targetWallet.deposit(money);

        this.transactions.add(WalletTransaction.create(this.publicId, this.money, TransactionType.TRANSFER));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 지갑에 지정된 금액 이상의 잔액이 있는지 확인합니다.
     *
     * @param requiredBalance 확인할 금액(long)
     * @return 충분한 잔액이 있으면 true, 아니면 false
     */
    public boolean hasEnoughBalance(long requiredBalance) {
        return this.money.getAmount() >= requiredBalance;
    }
}
