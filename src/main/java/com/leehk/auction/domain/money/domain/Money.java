package com.leehk.auction.domain.money.domain;

import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import jakarta.persistence.Embeddable;
import lombok.Getter;


/**
 * Money Value Object
 * <p>
 * 금액을 안전하게 표현하고, 음수 방지 및 연산 로직(add, subtract)을 캡슐화합니다.
 * Wallet 등 다른 도메인에서 금액 계산 시 사용됩니다.
 */
@Getter
@Embeddable
public class Money {

    private long amount;  // jpa 호환을 위해 final x

    protected Money() {}

    /**
     * Money 생성자
     *
     * @param amount 금액(long), 0 이상이어야 함
     * @throws CustomException amount가 0 미만일 경우 발생
     */
    public Money(long amount) {
        if (amount < 0)
            throw new CustomException(ErrorCode.INVALID_AMOUNT_INPUT);

        this.amount = amount;
    }

    /**
     * 현재 금액에 다른 Money 금액을 더한 새로운 Money 객체를 반환합니다.
     *
     * @param otherMoney 더할 Money 객체
     * @return 합산된 금액을 가진 새로운 Money 객체
     */
    public Money add(Money otherMoney) {
        return new Money(this.amount + otherMoney.amount);
    }

    /**
     * 현재 금액에서 다른 Money 금액을 뺀 새로운 Money 객체를 반환합니다.
     *
     * @param otherMoney 뺄 Money 객체
     * @return 차감된 금액을 가진 새로운 Money 객체
     * @throws CustomException 차감 후 금액이 음수가 되는 경우 발생
     */
    public Money subtract(Money otherMoney) {
        if (this.amount < otherMoney.amount)
            throw new CustomException(ErrorCode.AMOUNT_TOO_MANY);

        return new Money(this.amount - otherMoney.amount);
    }
}
