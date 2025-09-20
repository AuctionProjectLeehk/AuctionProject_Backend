package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.dto.AuctionDto;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.auction.infrastructure.AuctionRepository;
import com.leehk.auction.global.response.CustomException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuctionServiceImplTest {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private AuctionRepository auctionRepository;

    private Auction testAuction;

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
    }

    @Test
    @DisplayName("경매 생성 후 조회 성공")
    void createAndGetAuction() {
        // given
        Auction auction = testAuction;

        // when
        AuctionDto created = auctionService.createAuction(auction);
        AuctionDto found = auctionService.getAuction(created.getId());

        // then
        assertThat(found.getTitle()).isEqualTo("test 경매");
        assertThat(found.getCurrentPrice()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("입찰 성공 시 가격이 갱신")
    void placeBid_Success() {
        // given
        AuctionDto created = auctionService.createAuction(testAuction);

        // when
        AuctionDto updated = auctionService.placeBid(created.getId(), 11000L);

        // then
        assertThat(updated.getCurrentPrice()).isEqualTo(11000L);
    }

    @Test
    @DisplayName("여러 번 입찰 시 가격이 순차적으로 갱신")
    void multipleBids_Success() {
        // given
        AuctionDto created = auctionService.createAuction(testAuction);

        // when
        auctionService.placeBid(created.getId(), 11000L);
        AuctionDto afterFirstBid = auctionService.getAuction(created.getId());

        auctionService.placeBid(created.getId(), 12000L);
        AuctionDto afterSecondBid = auctionService.getAuction(created.getId());

        // then
        assertThat(afterFirstBid.getCurrentPrice()).isEqualTo(11000L);
        assertThat(afterSecondBid.getCurrentPrice()).isEqualTo(12000L);
    }

    @Test
    @DisplayName("경매 종료 성공")
    void endAuctionSuccess() {
        // given
        AuctionDto created = auctionService.createAuction(testAuction);

        // when
        AuctionDto ended = auctionService.endAuction(created.getId());

        // then
        assertThat(ended.getStatus()).isEqualTo(AuctionStatus.ENDED);
    }
    
    @Test
    @DisplayName("존재하지 않은 경매 조회 시 예외 발생")
    void getAuction_NotFound() {
        // given
        Long InvalidId = -1L;

        // when and then
        assertThatThrownBy(() -> auctionService.getAuction(InvalidId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("경매를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("종료된 경매에 입찰 시 예외 발생")
    void placeBid_OnEndedAuction() {
        // given
        AuctionDto created = auctionService.createAuction(testAuction);
        auctionService.endAuction(created.getId());

        // when and then
        assertThatThrownBy(() -> auctionService.placeBid(created.getId(), 11000L))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("이미 종료된 경매입니다.");
    }

    @Test
    @DisplayName("현재 가격보다 낮은 가격으로 입찰")
    void placeBid_LowerThanCurrent() {
        // given
        AuctionDto created = auctionService.createAuction(testAuction);

        // when and then
        assertThatThrownBy(() -> auctionService.placeBid(created.getId(), 9000L))
                .isInstanceOf(CustomException.class)
                .hasMessage("입찰 금액이 현재 최고가보다 낮습니다.");
    }

    @Test
    @DisplayName("경매 삭제 후 조회 예외 발생")
    void deleteAuction_ThenGetFail() {
        // given
        AuctionDto created = auctionService.createAuction(testAuction);

        // when
        auctionService.deleteAuction(created.getId());

        // then
        assertThatThrownBy(() -> auctionService.getAuction(created.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("경매를 찾을 수 없습니다.");
    }
}