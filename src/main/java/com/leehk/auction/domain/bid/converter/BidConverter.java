package com.leehk.auction.domain.bid.converter;


import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;

public class BidConverter {

    public static Bid entityToDomain(BidEntity bidEntity) {
        return Bid.builder()
                .id(bidEntity.getId())
                .bidderId(bidEntity.getBidderId())
                .bidPrice(bidEntity.getBidPrice())
                .bidTime(bidEntity.getBidTime())
                .auction(AuctionConverter.EntityToDomain(bidEntity.getAuctionEntity()))
                .build();
    }

    public static BidEntity domainToEntity(Bid bid) {
        return BidEntity.builder()
                .id(bid.getId())
                .bidderId(bid.getBidderId())
                .bidPrice(bid.getBidPrice())
                .bidTime(bid.getBidTime())
                .auctionEntity(AuctionConverter.DomainToEntity(bid.getAuction()))
                .build();
    }
}
