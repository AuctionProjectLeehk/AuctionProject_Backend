package com.leehk.auction.domain.money.domain;

import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    
    @Test
    @DisplayName("Money 생성 - 성공")
    void createMoney_Success() {
        // given
        long amount = 10000L;

        // when
        Money money = new Money(amount);

        // then
        assertThat(money.getAmount()).isEqualTo(amount);
    }
    
    @Test
    @DisplayName("Money 생성 - 실퍠: 음수 금액")
    void createMoney_Fail_NegativeAmount() {
        // given
        long amount = -1000L;

        // when and then
        assertThatThrownBy(()-> new Money(amount))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AMOUNT_INPUT.getMessage());
    }
    
    @Test
    @DisplayName("Money 추가 - 성공")
    void addMoney_Success() {
        // given
        Money money1 = new Money(10000L);
        Money money2 = new Money(12000L);

        // when
        Money addedMoney = money1.add(money2);

        // then
        // 불변성 확인
        assertThat(money1.getAmount()).isEqualTo(10000L);
        assertThat(money2.getAmount()).isEqualTo(12000L);
        
        // 결과 확인
        assertThat(addedMoney).isNotNull();
        assertThat(addedMoney.getAmount()).isEqualTo(money1.getAmount() + money2.getAmount());
    }
    
    @Test
    @DisplayName("Money 감소 - 성공")
    void subtractMoney_Success() {
        // given
        Money money1 = new Money(10000L);
        Money money2 = new Money(8000L);

        // when
        Money subtractedMoney = money1.subtract(money2);

        // then
        // 불변성 확인
        assertThat(money1.getAmount()).isEqualTo(10000L);
        assertThat(money2.getAmount()).isEqualTo(8000L);

        // 결과 확인
        assertThat(subtractedMoney).isNotNull();
        assertThat(subtractedMoney.getAmount()).isEqualTo(money1.getAmount() - money2.getAmount());
    }

    @Test
    @DisplayName("Money 감소 - 실패: 감소되는 돈이 너무 많음")
    void subtractMoney_Fail_AmountTooMany() {
        // given
        Money money1 = new Money(10000L);
        Money money2 = new Money(12000L);

        // when
        assertThatThrownBy(()-> money1.subtract(money2))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AMOUNT_TOO_MANY.getMessage());
    }
}