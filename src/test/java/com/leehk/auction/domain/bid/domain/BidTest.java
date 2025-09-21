package com.leehk.auction.domain.bid.domain;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
        Bid bid = Bid.builder()
                .id(1L)
                .bidderId(3L)
                .bidPrice(10000L)
                .auction(auction)
                .build();

        // then
        assertThat(bid.getBidderId()).isEqualTo(3L);
        assertThat(bid.getAuction()).isEqualTo(auction);
        assertThat(bid.getBidPrice()).isEqualTo(10000L);
        assertThat(bid.getId()).isEqualTo(1L);
        assertThat(bid.getBidTime()).isNotNull();
    }
}