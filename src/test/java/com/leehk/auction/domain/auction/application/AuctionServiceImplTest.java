package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AuctionServiceImplTest extends BaseH2Test {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private UserService userService;

    private Auction testAuction;
    private User testUser;

    @BeforeEach
    void setup() {
        testAuction = Auction.builder()
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();

        testUser = User.builder()
                .email("<EMAIL>")
                .name("test")
                .password("<PASSWORD>")
                .nickname("test")
                .build();
    }

    @Test
    @DisplayName("경매 생성 후 조회 성공")
    void createAndGetAuction() {
        // given
        Auction createdAuction = auctionService.createAuction(testAuction);

        // when
        Auction foundAuction = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(foundAuction.getTitle()).isEqualTo("test 경매");
        assertThat(foundAuction.getCurrentPrice()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("입찰 성공 시 가격이 갱신")
    void placeBid_Success() {
        // given
        Auction createdAuction = auctionService.createAuction(testAuction);
        User createdUser = userService.saveUser(testUser);

        // when
        Auction updatedAuction = auctionService.placeBid(createdAuction.getId(), createdUser.getId(), 11000L);

        // then
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(11000L);
    }

    @Test
    @DisplayName("여러 번 입찰 시 가격이 순차적으로 갱신")
    void multipleBids_Success() {
        // given
        Auction createdAuction = auctionService.createAuction(testAuction);
        User savedUser = userService.saveUser(testUser);

        // when
        auctionService.placeBid(createdAuction.getId(), savedUser.getId(), 11000L);
        Auction afterFirstBid = auctionService.getAuction(createdAuction.getId());

        auctionService.placeBid(createdAuction.getId(), savedUser.getId(), 12000L);
        Auction afterSecondBid = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(afterFirstBid.getCurrentPrice()).isEqualTo(11000L);
        assertThat(afterSecondBid.getCurrentPrice()).isEqualTo(12000L);
    }

    @Test
    @DisplayName("경매 종료 성공")
    void endAuctionSuccess() {
        // given
        Auction createdAuction = auctionService.createAuction(testAuction);

        // when
        Auction ended = auctionService.endAuction(createdAuction.getId());

        // then
        assertThat(ended.getStatus()).isEqualTo(AuctionStatus.ENDED);
    }
    
    @Test
    @DisplayName("존재하지 않은 경매 조회 시 예외 발생")
    void getAuction_NotfoundAuction() {
        // given
        Long InvalidId = -1L;

        // when and then
        assertThatThrownBy(() -> auctionService.getAuction(InvalidId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("종료된 경매에 입찰 시 예외 발생")
    void placeBid_OnEndedAuction() {
        // given
        Auction createdAuction = auctionService.createAuction(testAuction);
        auctionService.endAuction(createdAuction.getId());

        User savedUser = userService.saveUser(testUser);

        // when and then
        assertThatThrownBy(() -> auctionService.placeBid(createdAuction.getId(), savedUser.getId(),11000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_ALREADY_ENDED.getMessage());
    }

    @Test
    @DisplayName("현재 가격보다 낮은 가격으로 입찰")
    void placeBid_LowerThanCurrent() {
        // given
        Auction createdAuction = auctionService.createAuction(testAuction);
        User createduser = userService.saveUser(testUser);

        // when and then
        assertThatThrownBy(() -> auctionService.placeBid(createdAuction.getId(), createduser.getId(),9000L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BID_TOO_LOW.getMessage());
    }

    @Test
    @DisplayName("경매 삭제 후 조회 예외 발생")
    void deleteAuction_ThenGetFail() {
        // given
        Auction createdAuction = auctionService.createAuction(testAuction);

        // when
        auctionService.deleteAuction(createdAuction.getId());

        // then
        assertThatThrownBy(() -> auctionService.getAuction(createdAuction.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_NOT_FOUND.getMessage());
    }
}