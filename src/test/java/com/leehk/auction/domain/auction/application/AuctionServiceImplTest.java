package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.bid.domain.Bid;
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
import java.util.List;

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
    @DisplayName("경매 생성 후 조회 성공")
    void createAndGetAuction() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);

        System.out.println("savedOwnerUser = " + savedOwnerUser.getId() + ", " + savedOwnerUser.getNickname());

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        // when
        Auction foundAuction = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(foundAuction.getTitle()).isEqualTo("test 경매");
        assertThat(foundAuction.getCurrentPrice()).isEqualTo(10000L);
    }



    @Test
    @DisplayName("경매 종료 성공")
    void endAuctionSuccess() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

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
    @DisplayName("경매 삭제 후 조회 예외 발생")
    void deleteAuction_ThenGetFail() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        // when
        auctionService.deleteAuction(createdAuction.getId(), savedOwnerUser.getId());

        // then
        assertThatThrownBy(() -> auctionService.getAuction(createdAuction.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_NOT_FOUND.getMessage());
    }
}