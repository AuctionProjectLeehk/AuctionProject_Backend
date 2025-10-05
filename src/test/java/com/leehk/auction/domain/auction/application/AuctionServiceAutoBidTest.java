package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Transactional
class AuctionServiceAutoBidTest extends BaseH2Test {

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
                .owner(testUser)
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
    @DisplayName("자동 입찰 등록 성공")
    void registerAutoBid_Success() {
        // given
        User savedUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedUser.getId());

        // when
        auctionService.registerAutoBid(createdAuction.getId(), savedUser.getId(), 15000L);

        // then
        Auction updatedAuction = auctionService.getAuction(createdAuction.getId());
        assertThat(updatedAuction.getAutoBids().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("자동 입찰 등록 실패 - 경매 없음")
    void registerAutoBid_Fail_NotFoundAuction() {
        // given
        User savedUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedUser.getId());

        // when and then
        Long nonExistentAuctionId = -1L;
        assertThatThrownBy(() -> auctionService.registerAutoBid(nonExistentAuctionId, savedUser.getId(), 15000L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining(ErrorCode.AUCTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 등록 실패 - 사용자 없음")
    void registerAutoBid_Fail_NotFoundUser() {
        // given
        User savedUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedUser.getId());

        // when and then
        Long nonExistentUserId = -1L;
        assertThatThrownBy(() -> auctionService.registerAutoBid(createdAuction.getId(), nonExistentUserId, 15000L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 등록 실패 - 입찰가가 현재가 이하")
    void registerAutoBid_Fail_BidTooLow() {
        // given
        User savedUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedUser.getId());

        // when and then
        assertThatThrownBy(() -> auctionService.registerAutoBid(createdAuction.getId(), savedUser.getId(), 9000L))
                .isInstanceOf(Exception.class)
                .hasMessageContaining(ErrorCode.INVALID_AUTO_BID_CREATE.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 실행 성공 - 1개")
    void executeOneAutoBid_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedAutoBidderUser = userService.saveUser(makeUser(2L));

        long minGap = 1L;
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());
        auctionService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        // when
        auctionService.executeAutoBids(createdAuction.getId());

        // then
        Auction updatedAuction = auctionService.getAuction(createdAuction.getId());
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(10000L + minGap);
    }

    @Test
    @DisplayName("자동 입찰 실행 성공 - 2개 이상")
    void executeMultipleAutoBid_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedAutoBidderUser1 = userService.saveUser(makeUser(2L));
        User savedAutoBidderUser2 = userService.saveUser(makeUser(3L));

        long minGap = 1L;
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());
        auctionService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser1.getId(), 15000L);
        auctionService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser2.getId(), 20000L);

        // when
        auctionService.executeAutoBids(createdAuction.getId());

        // then
        Auction updatedAuction = auctionService.getAuction(createdAuction.getId());
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(15000L + minGap);
    }
}
