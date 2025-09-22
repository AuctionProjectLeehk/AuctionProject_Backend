package com.leehk.auction.domain.bid.application;

import com.leehk.auction.domain.auction.application.AuctionService;
import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.bid.converter.BidConverter;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;
import com.leehk.auction.domain.bid.infrastructure.BidRepository;
import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final AuctionService auctionService;

    @Override
    @Transactional
    public Bid placeBid(Long auctionId, Long bidderId, long bidPrice) {
        Auction auction = auctionService.getAuction(auctionId);

        Auction tmpAuction = auctionService.placeBid(auctionId, bidPrice);

        return BidConverter.entityToDomain(bidRepository.save(BidEntity.builder()
                .auctionEntity(AuctionConverter.DomainToEntity(auction))
                .bidderId(bidderId)
                .bidPrice(bidPrice)
                .build()
        ));
    }

    @Override
    public List<Bid> getBidByAuctionId(Long auctionId) {
        if (auctionService.getAuction(auctionId) == null)
            throw new CustomException(ErrorCode.AUCTION_NOT_FOUND);

        return bidRepository.findByAuctionEntity_Id(auctionId)
                .stream()
                .map(BidConverter::entityToDomain)
                .toList();
    }
}
