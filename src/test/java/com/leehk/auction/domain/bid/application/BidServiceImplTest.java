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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class BidServiceImplTest extends BaseH2Test {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private BidService bidService;

    @Test
    void placeBid_success() {
        // given
        Auction auction = Auction.builder()
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(1000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();

        AuctionEntity saveAuctionEntity = auctionRepository.save(AuctionConverter.DomainToEntity(auction));

        // when
        Bid bid = bidService.placeBid(saveAuctionEntity.getId(), 1L, 11000L);
        Optional<BidEntity> saveBidList = bidRepository.findById(bid.getId());

        // then
        assertThat(saveBidList.isPresent());
        assertThat(saveBidList.get().getBidPrice()).isEqualTo(11000L);
        assertThat(saveBidList.get().getAuctionEntity().getId()).isEqualTo(saveAuctionEntity.getId());
    }

    @Test
    void getBidByAuctionId_Success() {
        // given
        Auction auction = Auction.builder()
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(1000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();

        AuctionEntity saveAuction = auctionRepository.save(AuctionConverter.DomainToEntity(auction));
        System.out.println("auctionId: " + saveAuction.getId());

        // 3번 사람이 11000웡 입찰
        Bid bid1 = Bid.builder()
                .id(2L)
                .bidderId(5L)
                .bidPrice(11000L)
                .auction(AuctionConverter.EntityToDomain(saveAuction))
                .build();
        bidService.placeBid(saveAuction.getId(), bid1.getBidderId(), bid1.getBidPrice());

        // 7번 사람이 12000웡 입찰
        Bid bid2 = Bid.builder()
                .id(3L)
                .bidderId(7L)
                .bidPrice(12000L)
                .auction(AuctionConverter.EntityToDomain(saveAuction))
                .build();
        bidService.placeBid(saveAuction.getId(), bid2.getBidderId(), bid2.getBidPrice());

        // 3번 사람이 13000웡 입찰
        Bid bid3 = Bid.builder()
                .id(6L)
                .bidderId(5L)
                .bidPrice(13000L)
                .auction(AuctionConverter.EntityToDomain(saveAuction))
                .build();

        bidService.placeBid(saveAuction.getId(), bid3.getBidderId(), bid3.getBidPrice());

        // when
        List<Bid> bidList = bidService.getBidByAuctionId(saveAuction.getId());

        // then
        assertThat(bidList.size()).isEqualTo(3);  // 3번 입찰
        assertThat(saveAuction.getCurrentPrice()).isEqualTo(13000L);  // 13000원
    }
}