package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AuctionServiceBidTest extends BaseH2Test {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private UserService userService;

    private User testUser;
    private Auction testAuction;

    @BeforeEach
    void setup() {
        testUser = makeUser(1L);

        testAuction = Auction.builder()
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();
    }

    private User makeUser(long index) {
        return User.builder()
                .email("test" + index + "@example.com")
                .name("test" + index)
                .password("password")
                .nickname("test" + index)
                .build();
    }

    @Test
    @DisplayName("입찰 - 성공: 시 가격이 갱신")
    void placeBid_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser = userService.saveUser(makeUser(2L));

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        // when
        Auction updatedAuction = auctionService.placeBid(createdAuction.getId(), savedBidderUser.getId(), 11000L);

        // then
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(11000L);
    }

    @Test
    @DisplayName("입찰 - 성공: 여러 번 입찰 시 가격이 순차적으로 갱신")
    void placeBids_Success_multipleBid() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser1 = userService.saveUser(makeUser(2L));
        User savedBidderUser2 = userService.saveUser(makeUser(3L));

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        // when
        auctionService.placeBid(createdAuction.getId(), savedBidderUser1.getId(), 11000L);
        Auction afterFirstBid = auctionService.getAuction(createdAuction.getId());

        auctionService.placeBid(createdAuction.getId(), savedBidderUser2.getId(), 12000L);
        Auction afterSecondBid = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(afterFirstBid.getCurrentPrice()).isEqualTo(11000L);
        assertThat(afterSecondBid.getCurrentPrice()).isEqualTo(12000L);
    }


    @Test
    @DisplayName("입찰 - 실패: 종료된 경매에 입찰 시 예외 발생")
    void placeBid_Fail_OnEndedAuctionException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser = userService.saveUser(makeUser(2L));

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());
        auctionService.endAuction(createdAuction.getId());

        // when and then
        assertThatThrownBy(() -> auctionService.placeBid(createdAuction.getId(), savedBidderUser.getId(),11000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_ALREADY_ENDED.getMessage());
    }

    @Test
    @DisplayName("입찰 - 실패: 현재 가격보다 낮은 가격으로 입찰")
    void placeBid_Fail_LowerThanCurrentException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser = userService.saveUser(makeUser(2L));

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        // when and then
        assertThatThrownBy(() -> auctionService.placeBid(createdAuction.getId(), savedBidderUser.getId(),9000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BID_TOO_LOW.getMessage());
    }

    @Test
    @DisplayName("입찰 취소 테스트 - 성공")
    void cancelBid_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser1 = userService.saveUser(makeUser(2L));
        User savedBidderUser2 = userService.saveUser(makeUser(3L));
        User savedBidderUser3 = userService.saveUser(makeUser(4L));

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        auctionService.placeBid(createdAuction.getId(), savedBidderUser1.getId(), 11000L);
        auctionService.placeBid(createdAuction.getId(), savedBidderUser2.getId(), 12000L);
        auctionService.placeBid(createdAuction.getId(), savedBidderUser3.getId(), 13000L);

        Auction auction = auctionService.getAuction(createdAuction.getId());
        Bid highesetBid = auction.getHighestBid();

        // then
        auctionService.cancelBid(createdAuction.getId(), highesetBid.getId(), savedBidderUser3.getId());
        Auction updatedAuction = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(12000L);
        assertThat(updatedAuction.getBids().size()).isEqualTo(2);
    }

}
