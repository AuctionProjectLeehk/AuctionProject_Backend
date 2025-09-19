package com.leehk.auction.domain.auction.domain;

import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuctionTest {

    @Test
    @DisplayName("정상 입찰 - 현재가보다 높은 가격으로 입찰 성공")
    void placeBid_Success() {
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
        auction.placeBid(11000L);

        // then
        assertThat(auction.getCurrentPrice()).isEqualTo(11000L);
    }

    @Test
    @DisplayName("실패 - 현재가보다 낮은 가격으로 입찰 시 예외 발생")
    void placeBid_TooLow() {
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

        // when and then
        assertThatThrownBy(() -> auction.placeBid(9000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BID_TOO_LOW.getMessage());
    }
    
    @Test
    @DisplayName("실패 - 종료된 경매에 입찰 시 예외 발생")
    void placeBid_EndedAuction() {
        // given
        Auction auction = Auction.builder()
                .id(1L)
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ENDED)
                .build();

        // when and then
        assertThatThrownBy(() -> auction.placeBid(11000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_ALREADY_ENDED.getMessage());
    }

    @Test
    @DisplayName("경매 종료 성공")
    void endAuction_Success() {
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
        auction.endAuction();

        // then
        assertThat(auction.getStatus()).isEqualTo(AuctionStatus.ENDED);
    }
}