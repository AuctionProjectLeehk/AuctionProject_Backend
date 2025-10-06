package com.leehk.auction.domain.bid.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.auction.application.AuctionService;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.bid.domain.AutoBid;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class AutoBidServiceImplTest extends BaseH2Test {

    @Autowired
    private AutoBidService autoBidService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionService auctionService;

    private User testUser;
    private Auction testAuction;

    @BeforeEach
    void setup() {
        testUser = makeUser(1L);
        testAuction = makeAuction(1L);
    }

    private User makeUser(long index) {
        return User.builder()
                .email("test" + index + "@example.com")
                .name("test" + index)
                .password("password")
                .nickname("test" + index)
                .build();
    }

    private Auction makeAuction(long index) {
        return Auction.builder()
                .title("test 경매" + index)
                .description("test 설명" + index)
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();
    }

    @Test
    @DisplayName("경매 Id와 유저 Id를 이용하여 자동 입찰 찾기 - 성공")
    void getAutoBidByAuctionIdAndUserId_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        // when
        AutoBid savedAutoBid = autoBidService.getAutoBidByAuctionIdAndUserId(createdAuction.getId(), savedAutoBidderUser.getId());

        // then
        assertThat(savedAutoBid).isNotNull();
        assertThat(savedAutoBid.getAuctionId()).isEqualTo(createdAuction.getId());
        assertThat(savedAutoBid.getAutoBidderId()).isEqualTo(savedAutoBidderUser.getId());
    }

    @Test
    @DisplayName("경매 Id와 유저 Id를 이용하여 자동 입찰 찾기 - 실퍠: 경매 찾기 실패")
    void getAutoBidByAuction_Fail_NotFoundAuctionException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        // when and then
        assertThatThrownBy(() -> autoBidService.getAutoBidByAuctionIdAndUserId(-1L, savedAutoBidderUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("경매 Id와 유저 Id를 이용하여 자동 입찰 찾기 - 실퍠: 유저 찾기 실패")
    void getAutoBidByAuction_Fail_NotFoundUserException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        // when and then
        assertThatThrownBy(() -> autoBidService.getAutoBidByAuctionIdAndUserId(createdAuction.getId(), -1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("경매 Id와 유저 Id를 이용하여 자동 입찰 찾기 - 실퍠: 자동 입찰 찾기 실패")
    void getAutoBidByAuction_Fail_NotFoundAutoBidException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        User savedAnotherUser = userService.saveUser(makeUser(3L));

        // when and then
        assertThatThrownBy(() -> autoBidService.getAutoBidByAuctionIdAndUserId(createdAuction.getId(), savedAnotherUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUTO_BID_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("경매에 존재하는 모든 자동 입찰 찾기 - 성공")
    void getAutoBidsByAuctionId_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser1 = userService.saveUser(makeUser(2L));
        User savedAutoBidderUser2 = userService.saveUser(makeUser(3L));
        User savedAutoBidderUser3 = userService.saveUser(makeUser(4L));
        User savedAutoBidderUser4 = userService.saveUser(makeUser(5L));

        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser1.getId(), 15000L);
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser2.getId(), 16000L);
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser3.getId(), 13000L);
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser4.getId(), 12000L);
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser1.getId(), 17000L);

        // when
        List<AutoBid> autoBids = autoBidService.getAutoBidsByAuctionId(createdAuction.getId());

        // then
        assertThat(autoBids.size()).isEqualTo(4);
        assertThat(autoBids.stream()
                .mapToLong(AutoBid::getMaxAutoBidPrice)
                .max()
                .orElse(0L)
        ).isEqualTo(17000L);
    }

    @Test
    @DisplayName("경매에 존재하는 모든 자동 입찰 찾기 - 실패: 경매 찾기 실패")
    void getAutoBidsByAuctionId_Fail_NotFoundAuctionException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        // when and then
        assertThatThrownBy(() -> autoBidService.getAutoBidsByAuctionId(-1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("등록된 자동 입찰 찾기 - 성공")
    void getAutoBidById_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        AutoBid registeredAutoBid = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        // when
        AutoBid savedAutoBid = autoBidService.getAutoBidById(registeredAutoBid.getId());

        // then
        assertThat(savedAutoBid).isNotNull();
        assertThat(savedAutoBid.getAutoBidderId()).isEqualTo(savedAutoBidderUser.getId());
    }

    @Test
    @DisplayName("등록된 자동 입찰 찾기 - 실패: 자동 입찰 찾기 실패")
    void getAutoBidById_Fail_NotFoundAutoBidException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        // when and then
        assertThatThrownBy(() -> autoBidService.getAutoBidById(UUID.randomUUID()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUTO_BID_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 등록 - 성공")
    void registerAutoBid_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser1 = userService.saveUser(makeUser(2L));
        User savedAutoBidderUser2 = userService.saveUser(makeUser(3L));

        // when
        AutoBid autoBid1 = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser1.getId(), 15000L);
        AutoBid autoBid2 = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser2.getId(), 16000L);
        AutoBid autoBid3 = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser1.getId(), 14000L);

        // then
        assertThat(autoBid1.getAutoBidderId()).isEqualTo(savedAutoBidderUser1.getId());
        assertThat(autoBid2.getAutoBidderId()).isEqualTo(savedAutoBidderUser2.getId());
        assertThat(autoBid3.getAutoBidderId()).isEqualTo(savedAutoBidderUser1.getId());
    }

    @Test
    @DisplayName("자동 입찰 비활성화 - 성공")
    void deactivateAutoBid_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        AutoBid savedAutoBid = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        // when
        autoBidService.deactivateAutoBid(savedAutoBid.getId(), createdAuction.getId(), savedAutoBidderUser.getId());

        // then
        AutoBid deactivatedAutoBid = autoBidService.getAutoBidById(savedAutoBid.getId());
        assertThat(deactivatedAutoBid).isNotNull();
        assertThat(deactivatedAutoBid.isActive()).isFalse();
    }

    @Test
    @DisplayName("자동 입찰 비활성화 - 실패: 경매 또는 유저 찾기 실패")
    void deactivateAutoBid_Fail_NotFoundActionOrUserException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        AutoBid savedAutoBid = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        // when and then
        assertThatThrownBy(() -> autoBidService.deactivateAutoBid(savedAutoBid.getId(), -1L, savedAutoBidderUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUCTION_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> autoBidService.deactivateAutoBid(savedAutoBid.getId(), createdAuction.getId(), -1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 비활성화 - 실패: 자동 입찰 찾기 실패")
    void deactivateAutoBid_Fail_NotFoundAutoBidException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());
        Auction createdAnotherAuction = auctionService.createAuction(makeAuction(2L), savedOwnerUser.getId());

        User savedAutoBidderUser = userService.saveUser(makeUser(2L));
        AutoBid savedAutoBid = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser.getId(), 15000L);

        User savedAnotherUser = userService.saveUser(makeUser(3L));

        // when and then
        assertThatThrownBy(() -> autoBidService.deactivateAutoBid(savedAutoBid.getId(), createdAuction.getId(), savedAnotherUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUTO_BID_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> autoBidService.deactivateAutoBid(savedAutoBid.getId(), createdAnotherAuction.getId(), savedOwnerUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUTO_BID_NOT_FOUND.getMessage());

        assertThatThrownBy(() -> autoBidService.deactivateAutoBid(UUID.randomUUID(), createdAuction.getId(), savedOwnerUser.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUTO_BID_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("자동 입찰 비활성화 - 실패: 올바른 자동 입찰에 접근 실패")
    void deactivateAutoBid_Fail_InvalidAutoBidException() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        User savedAutoBidderUser1 = userService.saveUser(makeUser(2L));
        AutoBid savedAutoBid1 = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser1.getId(), 15000L);

        User savedAutoBidderUser2 = userService.saveUser(makeUser(3L));
        AutoBid savedAutoBid2 = autoBidService.registerAutoBid(createdAuction.getId(), savedAutoBidderUser2.getId(), 16000L);

        // when and then
        assertThatThrownBy(() -> autoBidService.deactivateAutoBid(savedAutoBid1.getId(), createdAuction.getId(), savedAutoBidderUser2.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AUTO_BID_ACCESS.getMessage());

        assertThatThrownBy(() -> autoBidService.deactivateAutoBid(savedAutoBid2.getId(), createdAuction.getId(), savedAutoBidderUser1.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_AUTO_BID_ACCESS.getMessage());
    }
}