package com.leehk.auction.domain.auction.domain;

import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.bid.domain.AutoBid;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class AuctionTest {

    private Auction testAuction;

    @BeforeEach
    void setup() {
        testAuction = Auction.builder()
                .id(new Random().nextLong())
                .owner(User.builder().id(100000L).build())
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();
    }

    @Test
    @DisplayName("정상 입찰 - 현재가보다 높은 가격으로 입찰 성공")
    void placeBid_Success() {
        // given
        Auction auction = testAuction;

        // when
        auction.placeBid(1L, 11000L);

        // then
        assertThat(auction.getCurrentPrice()).isEqualTo(11000L);
    }

    @Test
    @DisplayName("정상 입찰 - 입찰자 확인")
    void placeBid_Success_Bidder() {
        // given
        Auction auction = testAuction;

        // when
        auction.placeBid(1L, 11000L);

        // then
        assertThat(auction.getBids().get(0).getBidderId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("정상 입찰 - 입찰자가 여러명인 경우, 총 인원과 최대 입찰자 확인")
    void getBidsAndGetHighestBidderIdTest() {
        // given
        Auction auction = testAuction;

        // when
        auction.placeBid(1L, 11000L);
        auction.placeBid(2L, 12000L);
        auction.placeBid(7L, 13000L);
        auction.placeBid(1L, 14000L);

        // then
        assertThat(auction.getBids().size()).isEqualTo(4);  // 4개 입찰
        assertThat(auction.getBids().stream().map(Bid::getBidderId).distinct().count()).isEqualTo(3);  // 3명 입찰
        assertThat(auction.getHighestBidderId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("실패 - 소유주가 입찰 시 예외 발생")
    void placeBid_Fail_OwnerBid() {
        // given
        Auction auction = testAuction;

        // when and then
        assertThatThrownBy(() -> auction.placeBid(auction.getOwner().getId(), 11000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.OWNER_CANNOT_BID.getMessage());
    }

    @Test
    @DisplayName("정상 입찰 취소")
    void cancelBid_Success() {
        // given
        Auction auction = testAuction;

        auction.placeBid(1L, 11000L);
        auction.placeBid(2L, 12000L);
        auction.placeBid(7L, 13000L);
        auction.placeBid(1L, 14000L);

        List<Bid> bids = auction.getBids();
        Bid highestBid = bids.get(bids.size() - 1);

        // when
        auction.cancelBid(highestBid.getId(), 1L);

        // then
        assertThat(auction.getBids().size()).isEqualTo(3);
        assertThat(auction.getCurrentPrice()).isEqualTo(13000L);
    }
    
    @Test
    @DisplayName("실패 - 없는 입찰 Id 로 접근")
    void cancelBid_NotFoundBid() {
        // given
        Auction auction = testAuction;

        auction.placeBid(1L, 11000L);
        auction.placeBid(2L, 12000L);
        auction.placeBid(7L, 13000L);
        auction.placeBid(1L, 14000L);

        // when and then
        assertThatThrownBy(() -> auction.cancelBid(UUID.randomUUID(), 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BID_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("실패 - 입찰 Id와 입찰자 Id가 일치하지 않음")
    void cancelBid_UnAuthorizedBid() {
        // given
        Auction auction = testAuction;

        auction.placeBid(1L, 11000L);
        auction.placeBid(2L, 12000L);
        auction.placeBid(7L, 13000L);
        auction.placeBid(1L, 14000L);

        List<Bid> bids = auction.getBids();
        Bid highestBid = bids.get(bids.size() - 1);

        // when and then
        assertThatThrownBy(() -> auction.cancelBid(highestBid.getId(), 2L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_BID_ACTION.getMessage());
    }

    @Test
    @DisplayName("실패 - 현재가보다 낮은 가격으로 입찰 시 예외 발생")
    void placeBid_TooLow() {
        // given
        Auction auction = testAuction;

        // when and then
        assertThatThrownBy(() -> auction.placeBid(1L, 9000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BID_TOO_LOW.getMessage());
    }
    
    @Test
    @DisplayName("실패 - 종료된 경매에 입찰 시 예외 발생")
    void placeBid_EndedAuction() {
        // given
        Auction auction = testAuction;

        // when
        auction.endAuction();

        // then
        assertThatThrownBy(() -> auction.placeBid(1L, 11000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_ALREADY_ENDED.getMessage());
    }

    @Test
    @DisplayName("경매 종료 성공")
    void endAuction_Success() {
        // given
        Auction auction = testAuction;

        // when
        auction.endAuction();

        // then
        assertThat(auction.getStatus()).isEqualTo(AuctionStatus.ENDED);
    }

    @Test
    @DisplayName("Auction에 새로운 자동 입찰 등록 성공")
    void registerNewAutoBid_Success() {
        // given
        Auction auction = testAuction;

        // when
        AutoBid autoBid = auction.registerAutoBid(2L, 20000L);

        // then
        assertThat(autoBid).isNotNull();
        assertThat(autoBid.getMaxAutoBidPrice()).isEqualTo(20000L);
        assertThat(auction.getAutoBids().size()).isEqualTo(1);
        assertThat(auction.getAutoBids()).contains(autoBid);
    }

    @Test
    @DisplayName("Auction에 기존 UserId의 자동 입찰이 있는 경우, 그 UserId의 최대 입찰가 업데이트 성공")
    void registerUpdateAutoBid_Success() {
        // given
        Auction auction = testAuction;
        AutoBid autoBid1 = auction.registerAutoBid(2L, 20000L);
        AutoBid autoBid2 = auction.registerAutoBid(3L, 30000L);

        // when
        AutoBid updatedAutoBid = auction.registerAutoBid(2L, 25000L);

        // then
        assertThat(auction.getAutoBids().size()).isEqualTo(2);
        assertThat(auction.getAutoBids()).doesNotContain(autoBid1);
        assertThat(auction.getAutoBids()).contains(updatedAutoBid);
        assertThat(auction.getAutoBids()).contains(autoBid2);
    }
    
    @Test
    @DisplayName("Auction에 자동 입찰 등록 실패 - 소유주가 등록 시도")
    void registerAutoBid_Fail_OwnerRegister() {
        // given
        Auction auction = testAuction;

        // when and then
        assertThatThrownBy(() -> auction.registerAutoBid(auction.getOwner().getId(), 20000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.OWNER_CANNOT_AUTO_BID.getMessage());
    }

    @Test
    @DisplayName("Auction에 자동 입찰 등록 실패 - 현재가보다 최대 입찰가 설정 에러")
    void registerAutoBid_Fail() {
        // given
        Auction auction = testAuction;

        // when and then
        assertThatThrownBy(() -> auction.registerAutoBid(2L, 9999L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AUTO_BID_CREATE.getMessage());

        assertThatThrownBy(() -> auction.registerAutoBid(3L, 10000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AUTO_BID_CREATE.getMessage());
    }

    @Test
    @DisplayName("기존 자동 입찰이 있는 경우, 최대 입찰가 업데이트 성공")
    void registerAutoBid_existingAutoBid_shouldUpdateMaxPrice() {
        // given
        Auction auction = testAuction;
        auction.registerAutoBid(2L, 20000L);

        // when
        AutoBid updatedAutoBid = auction.registerAutoBid(2L, 25000L);

        // then
        assertThat(updatedAutoBid.getMaxAutoBidPrice()).isEqualTo(25000L);
        assertThat(auction.getAutoBids()).contains(updatedAutoBid);
        assertThat(auction.getAutoBids().size()).isEqualTo(1); // 여전히 하나의 자동 입찰만 존재
    }

    @Test
    @DisplayName("활성화된 자동 입찰이 있는 경우, 최대 입찰가까지 자동으로 입찰 성공")
    void executeAutoBids_shouldPlaceBidsUpToMaxPrice() {
        // given
        Auction auction = testAuction;
        auction.registerAutoBid(2L, 20000L);
        auction.registerAutoBid(3L, 25000L);

        // when
        List<Bid> newBids = auction.executeAutoBids();
        AutoBid highestAutoBid = auction.getAutoBids().stream()
                .filter(ab -> ab.getAutoBidderId().equals(3L))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND));

        // then
        assertThat(newBids).isNotNull();
        assertThat(auction.getCurrentPrice()).isEqualTo(25000L);
        assertThat(highestAutoBid.getCurrentAutoBidPrice()).isEqualTo(25000L);
    }

    @Test
    @DisplayName("최고 자동 입찰자 외의 모든 자동 입찰 비활성화 성공")
    void executeAutoBids_shouldAutoBidsDeactivateWithoutTopAutoBid() {
        // given
        Auction auction = testAuction;
        auction.registerAutoBid(2L, 20000L);
        auction.registerAutoBid(3L, 25000L);
        auction.registerAutoBid(4L, 30000L);
        auction.registerAutoBid(5L, 35000L);
        auction.registerAutoBid(6L, 40000L);

        // when
        List<Bid> newBids = auction.executeAutoBids();

        // then
        assertThat(auction.getAutoBids())
                .filteredOn(ab -> ab.getAutoBidderId().equals(6L))
                .extracting(AutoBid::isActive)
                .containsOnly(true);

        assertThat(auction.getAutoBids())
                .filteredOn(ab -> !ab.getAutoBidderId().equals(6L))
                .extracting(AutoBid::isActive)
                .containsOnly(false);
    }

    @Test
    @DisplayName("활성화된 자동 입찰이 없는 경우, 아무 동작도 하지 않음")
    void executeAutoBids_noActiveAutoBids_shouldDoNothing() {
        Auction auction = testAuction;

        // when
        List<Bid> newBids = auction.executeAutoBids();

        // then
        assertThat(newBids).isEmpty();
    }

    @Test
    @DisplayName("유저 Id로 자동 입찰 비활성화 성공")
    void deactivateAutoBid_shouldDeactivateExistingAutoBidByUserId() {
        // given
        Auction auction = testAuction;
        auction.registerAutoBid(2L, 20000L);

        // when
        auction.deactivateAutoBidByUserId(2L);

        // then
        AutoBid deactivatedAutoBid = auction.getAutoBids().stream()
                .filter(ab -> ab.getAutoBidderId().equals(2L))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND));

        assertThat(deactivatedAutoBid.isActive()).isFalse();
    }

    @Test
    @DisplayName("유저 Id로 자동 입찰 비활성화 실패 - 해당 유저 Id의 자동 입찰이 없는 경우")
    void deactivateAutoBid_shouldThrowExceptionWhenUserIdNotFound() {
        // given
        Auction auction = testAuction;
        auction.registerAutoBid(2L, 20000L);
        auction.registerAutoBid(3L, 25000L);
        auction.registerAutoBid(4L, 30000L);

        // when and then
        assertThatThrownBy(() -> auction.deactivateAutoBidByUserId(-1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUTO_BID_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 Id로 자동 입찰 비활성화 성공")
    void deactivateAutoBid_shouldDeactivateExistingAutoBidByAutoBidId() {
        // given
        Auction auction = testAuction;
        AutoBid autoBid1 = auction.registerAutoBid(2L, 20000L);
        AutoBid autoBid2 = auction.registerAutoBid(3L, 25000L);
        AutoBid autoBid3 = auction.registerAutoBid(4L, 30000L);

        // when
        auction.deactivateAutoBidByAutoBidId(autoBid1.getId());

        // then
        AutoBid deactivatedAutoBid = auction.getAutoBids().stream()
                .filter(ab -> ab.getId().equals(autoBid1.getId()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.AUTO_BID_NOT_FOUND));

        assertThat(deactivatedAutoBid.isActive()).isFalse();
    }

    @Test
    @DisplayName("자동 입찰 Id로 자동 입찰 비활성화 실패 - 해당 자동 입찰 Id가 없는 경우")
    void deactivateAutoBid_shouldThrowExceptionWhenAutoBidIdNotFound() {
        // given
        Auction auction = testAuction;
        auction.registerAutoBid(2L, 20000L);
        auction.registerAutoBid(3L, 25000L);
        auction.registerAutoBid(4L, 30000L);

        // when and then
        assertThatThrownBy(() -> auction.deactivateAutoBidByAutoBidId(UUID.randomUUID()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUTO_BID_NOT_FOUND.getMessage());
    }
}