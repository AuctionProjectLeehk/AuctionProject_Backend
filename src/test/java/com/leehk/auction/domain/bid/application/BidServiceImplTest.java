package com.leehk.auction.domain.bid.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.auction.application.AuctionService;
import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.auction.infrastructure.AuctionRepository;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;
import com.leehk.auction.domain.bid.infrastructure.BidRepository;
import com.leehk.auction.domain.user.application.UserService;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.global.response.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class BidServiceImplTest extends BaseH2Test {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private BidService bidService;

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
    @DisplayName("입찰 성공 - 현재가보다 높은 금액으로 입찰")
    void placeBid_success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser = userService.saveUser(makeUser(2L));
        
        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        // when
        Bid bid = bidService.placeBid(createdAuction.getId(), savedBidderUser.getId(), 11000L);
        Auction updatedAuction = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(bid.getBidPrice()).isEqualTo(11000L);
        assertThat(bid.getBidderId()).isEqualTo(savedBidderUser.getId());

        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(11000L);
    }

    @Test
    void getBidByAuctionId_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser1 = userService.saveUser(makeUser(2L));
        User savedBidderUser2 = userService.saveUser(makeUser(3L));
        User savedBidderUser3 = userService.saveUser(makeUser(4L));

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());


        // when
        bidService.placeBid(createdAuction.getId(), savedBidderUser1.getId(), 11000L);
        bidService.placeBid(createdAuction.getId(), savedBidderUser2.getId(), 12000L);
        bidService.placeBid(createdAuction.getId(), savedBidderUser3.getId(), 13000L);
        bidService.placeBid(createdAuction.getId(), savedBidderUser2.getId(), 14000L);

        List<Bid> bidList = bidService.getBidByAuctionId(createdAuction.getId());
        Auction updatedAuction = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(bidList.size()).isEqualTo(4);  // 4번 입찰
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(14000L);  // 14000원
    }

    @Test
    @DisplayName("동일 사용자가 여러번 입찰 - 가장 높은 입찰만 확인")
    void placeMultipleBids_sameUser() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser = userService.saveUser(makeUser(2L));

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        // when - 여러번 입찰
        bidService.placeBid(createdAuction.getId(), savedBidderUser.getId(), 11000L);
        bidService.placeBid(createdAuction.getId(), savedBidderUser.getId(), 12000L);
        bidService.placeBid(createdAuction.getId(), savedBidderUser.getId(), 13000L);

        List<Bid> bidList = bidService.getBidByAuctionId(createdAuction.getId());
        Auction updatedAuction = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(bidList.size()).isEqualTo(3);
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(13000L);
        assertThat(updatedAuction.getHighestBidderId()).isEqualTo(savedBidderUser.getId());
    }

    @Test
    @DisplayName("입찰 취소 테스트")
    void cancelBid_Success() {
        // given
        User savedOwnerUser = userService.saveUser(testUser);
        User savedBidderUser1 = userService.saveUser(makeUser(2L));
        User savedBidderUser2 = userService.saveUser(makeUser(3L));

        Auction createdAuction = auctionService.createAuction(testAuction, savedOwnerUser.getId());

        bidService.placeBid(createdAuction.getId(), savedBidderUser1.getId(), 11000L);
        bidService.placeBid(createdAuction.getId(), savedBidderUser2.getId(), 12000L);
        Bid highestBid = bidService.getHighestBid(createdAuction.getId());

        // when
        bidService.cancelBid(highestBid.getId(), savedBidderUser2.getId());

        List<Bid> bidList = bidService.getBidByAuctionId(createdAuction.getId());
        Auction updatedAuction = auctionService.getAuction(createdAuction.getId());

        // then
        assertThat(bidList.size()).isEqualTo(1);
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(11000L);
    }

    @Test
    @DisplayName("에러 - 존재하지 않는 입찰 Id 로 접근")
    void getBid_NotFoundBid() {
        // given
        UUID InvalidId = UUID.randomUUID();

        // when and then
        assertThatThrownBy(() -> bidService.getBidByBidId(InvalidId))
                .isInstanceOf(CustomException.class)
                .hasMessage("입찰을 찾을 수 없습니다.");

    }
}