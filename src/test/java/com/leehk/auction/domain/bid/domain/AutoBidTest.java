package com.leehk.auction.domain.bid.domain;

import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class AutoBidTest {

    @Test
    @DisplayName("자동 입찰 생성 성공 - default 값 확인")
    void create_Success() {
        // given
        AutoBid autoBid = AutoBid.create(1L, 1L, 5000L, 1000L);

        // then
        assertThat(autoBid.getId()).isNotNull();
        assertThat(autoBid.isActive()).isTrue();
    }

    @Test
    @DisplayName("자동 입찰 생성 실패 - 현재가가 최대 입찰가 초과")
    void create_Fail_CurrentPriceExceedsMax() {
        // when and then
        assertThatThrownBy(() -> AutoBid.create(1L, 1L, 5000L, 6000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AUTO_BID_CURRENT_PRICE.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 현재 입찰가 수정 성공")
    void updateCurrentAutoBidPrice_Success() {
        // given
        AutoBid autoBid = AutoBid.create(1L, 1L, 5000L, 1000L);

        // when
        AutoBid updatedAutoBid = autoBid.updateCurrentAutoBidPrice(3000L);

        // then
        assertThat(updatedAutoBid.getCurrentAutoBidPrice()).isEqualTo(3000L);
    }

    @Test
    @DisplayName("자동 입찰 현재 입찰가 수정 실패 - 최대 입찰가 초과")
    void updateCurrentAutoBidPrice_Fail_MaxPriceExceeded() {
        // given
        AutoBid autoBid = AutoBid.create(1L, 1L, 5000L, 1000L);

        // when and then
        assertThatThrownBy(() -> autoBid.updateCurrentAutoBidPrice(10000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AUTO_BID_CURRENT_PRICE.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 최대 입찰가 수정 성공")
    void updateMaxAutoBidPrice_Success() {
        // given
        AutoBid autoBid = AutoBid.create(1L, 1L, 5000L, 1000L);
        AutoBid updatedAutoBid1 = autoBid.updateCurrentAutoBidPrice(3000L);

        // when
        AutoBid updatedAutoBid2 = updatedAutoBid1.updateMaxAutoBidPrice(10000L);

        // then
        assertThat(updatedAutoBid2.getMaxAutoBidPrice()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("자동 입찰 최대 입찰가 수정 실패 - 현재 입찰가 미만")
    void updateMaxAutoBidPrice_Fail_CurrentPriceFail() {
        // given
        AutoBid autoBid = AutoBid.create(1L, 1L, 5000L, 1000L);
        AutoBid updatedAutoBid = autoBid.updateCurrentAutoBidPrice(3000L);

        // when and then
        assertThatThrownBy(() -> updatedAutoBid.updateMaxAutoBidPrice(2000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AUTO_BID_MAX_PRICE.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 비활성화 성공")
    void deactivate_Success() {
        // given
        AutoBid autoBid = AutoBid.create(1L, 1L, 5000L, 1000L);

        // when
        AutoBid deactivated = autoBid.deactivate();

        // then
        assertThat(deactivated.isActive()).isFalse();
    }

    @Test
    @DisplayName("복원 테스트 성공")
    void restore_Success() {
        // given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // when
        AutoBid restored = AutoBid.restore(id, 1L, 1L,
                5000L, 2000L, true,
                now.minusDays(1), now);

        // then
        assertThat(restored.getId()).isEqualTo(id);
        assertThat(restored.getAuctionId()).isEqualTo(1L);
        assertThat(restored.getUpdatedAt()).isEqualTo(now);

    }
}