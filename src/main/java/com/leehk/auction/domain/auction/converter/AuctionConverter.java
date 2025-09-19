package com.leehk.auction.domain.auction.converter;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;

public class AuctionConverter {

    public static Auction EntityToDomain(AuctionEntity auctionEntity) {
        return Auction.builder()
                .id(auctionEntity.getId())
                .title(auctionEntity.getTitle())
                .description(auctionEntity.getDescription())
                .startPrice(auctionEntity.getStartPrice())
                .currentPrice(auctionEntity.getCurrentPrice())
                .startTime(auctionEntity.getStartTime())
                .endTime(auctionEntity.getEndTime())
                .status(auctionEntity.getStatus())
                .build();
    }
}
