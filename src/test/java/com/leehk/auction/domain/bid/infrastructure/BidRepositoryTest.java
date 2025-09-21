package com.leehk.auction.domain.bid.infrastructure;

import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.auction.infrastructure.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BidRepositoryTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Test
    @DisplayName("bid db에 한명 저장 테스트")
    void saveAndFindBidTest() {
        //given
        Auction auction = Auction.builder()
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();

        AuctionEntity auctionEntity = AuctionConverter.DomainToEntity(auction);
        AuctionEntity savedAuctionEntity = auctionRepository.save(auctionEntity);

        BidEntity bidEntity = BidEntity.builder()
                .bidderId(1L)
                .bidPrice(1000L)
                .bidTime(LocalDateTime.now())
                .auctionEntity(savedAuctionEntity)
                .build();

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
        Auction auction = Auction.builder()
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();

        AuctionEntity auctionEntity = AuctionConverter.DomainToEntity(auction);
        AuctionEntity savedAuctionEntity = auctionRepository.save(auctionEntity);

        BidEntity bidEntity1 = BidEntity.builder()
                .bidderId(1L)
                .bidPrice(1000L)
                .bidTime(LocalDateTime.now())
                .auctionEntity(savedAuctionEntity)
                .build();

        BidEntity bidEntity2 = BidEntity.builder()
                .bidderId(2L)
                .bidPrice(1100L)
                .bidTime(LocalDateTime.now())
                .auctionEntity(savedAuctionEntity)
                .build();

        BidEntity bidEntity3 = BidEntity.builder()
                .bidderId(1L)
                .bidPrice(1200L)
                .bidTime(LocalDateTime.now())
                .auctionEntity(savedAuctionEntity)
                .build();

        // when
        BidEntity saved1 = bidRepository.save(bidEntity1);
        BidEntity saved2 = bidRepository.save(bidEntity2);
        BidEntity saved3 = bidRepository.save(bidEntity3);

        List<BidEntity> bidEntities = bidRepository.findByAuctionEntity_Id(savedAuctionEntity.getId());

        // then
        assertThat(bidEntities.size()).isEqualTo(3);
    }
}