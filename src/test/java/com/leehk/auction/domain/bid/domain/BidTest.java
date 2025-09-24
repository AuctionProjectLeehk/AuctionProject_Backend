package com.leehk.auction.domain.bid.domain;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BidTest {

    @Test
    @DisplayName("Bid 생성 테스트")
    void createBidTest() {
        // given
        Auction auction = Auction.builder()
                .id(1L)
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();

        // when
        Bid bid = Bid.create(
                3L,
                1L,
                10000L
        );

        // then
        assertThat(bid.getBidderId()).isEqualTo(3L);
        assertThat(bid.getAuctionId()).isEqualTo(auction.getId());
        assertThat(bid.getBidPrice()).isEqualTo(10000L);
        assertThat(bid.getBidTime()).isNotNull();
    }
}