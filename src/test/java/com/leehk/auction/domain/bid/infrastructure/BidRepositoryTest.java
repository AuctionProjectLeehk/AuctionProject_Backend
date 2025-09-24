package com.leehk.auction.domain.bid.infrastructure;

import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.auction.infrastructure.AuctionRepository;
import com.leehk.auction.domain.bid.converter.BidConverter;
import com.leehk.auction.domain.bid.domain.Bid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BidRepositoryTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

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
    @DisplayName("bid db에 한명 저장 테스트")
    void saveAndFindBidTest() {
        //given
        AuctionEntity auctionEntity = AuctionConverter.domainToEntity(testAuction);
        AuctionEntity savedAuctionEntity = auctionRepository.save(auctionEntity);

        Auction savedAuction = AuctionConverter.entityToDomain(savedAuctionEntity);
        BidEntity bidEntity = BidConverter.domainToEntity(Bid.create(1L, savedAuctionEntity.getId(), 1000L), savedAuction);

        // when
        BidEntity saved = bidRepository.save(bidEntity);
        Optional<BidEntity> found = bidRepository.findById(saved.getId());
        
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getBidPrice()).isEqualTo(1000L);
    }
    
    @Test
    @DisplayName("bid db 에 여러명 저장 후 확인 테스트")
    void saveAndFindAllBidTest() {
        //given
        AuctionEntity auctionEntity = AuctionConverter.domainToEntity(testAuction);
        AuctionEntity savedAuctionEntity = auctionRepository.save(auctionEntity);

        Auction savedAuction = AuctionConverter.entityToDomain(savedAuctionEntity);
        BidEntity bidEntity1 = BidConverter.domainToEntity(Bid.create(1L, savedAuctionEntity.getId(), 1000L), savedAuction);
        BidEntity bidEntity2 = BidConverter.domainToEntity(Bid.create(2L, savedAuctionEntity.getId(), 1100L), savedAuction);
        BidEntity bidEntity3 = BidConverter.domainToEntity(Bid.create(3L, savedAuctionEntity.getId(), 1200L), savedAuction);

        // when
        BidEntity saved1 = bidRepository.save(bidEntity1);
        BidEntity saved2 = bidRepository.save(bidEntity2);
        BidEntity saved3 = bidRepository.save(bidEntity3);

        List<BidEntity> bidEntities = bidRepository.findByAuctionEntity_Id(savedAuctionEntity.getId());

        // then
        assertThat(bidEntities.size()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("최고 입찰 조회 테스트")
    void findTopBidTest() {
        // given
        AuctionEntity savedAuctionEntity = auctionRepository.save(AuctionConverter.domainToEntity(testAuction));

        Auction savedAuction = AuctionConverter.entityToDomain(savedAuctionEntity);
        BidEntity bidEntity1 = BidConverter.domainToEntity(Bid.create(1L, savedAuctionEntity.getId(), 1000L), savedAuction);
        BidEntity bidEntity2 = BidConverter.domainToEntity(Bid.create(2L, savedAuctionEntity.getId(), 1100L), savedAuction);
        BidEntity bidEntity3 = BidConverter.domainToEntity(Bid.create(1L, savedAuctionEntity.getId(), 1200L), savedAuction);

        BidEntity bid1 = bidRepository.save(bidEntity1);
        BidEntity bid2 = bidRepository.save(bidEntity2);
        BidEntity bid3 = bidRepository.save(bidEntity3);

        // when
        Optional<BidEntity> highestBid = bidRepository.findTopByAuctionEntity_IdOrderByBidPriceDesc(savedAuctionEntity.getId());

        // then
        assertThat(highestBid).isPresent();
        assertThat(highestBid.get().getBidPrice()).isEqualTo(1200L);
        assertThat(highestBid.get().getBidderId()).isEqualTo(1L);
    }
}