package com.leehk.auction.domain.bid.converter;

import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.dto.BidRequestDto;
import com.leehk.auction.domain.bid.dto.BidResponseDto;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;

public class BidConverter {

    public static Bid entityToDomain(BidEntity bidEntity) {
        return Bid.restore(
                bidEntity.getId(),
                bidEntity.getBidderId(),
                bidEntity.getAuctionEntity().getId(),
                bidEntity.getBidPrice(),
                bidEntity.getBidTime()
        );
    }

    public static BidEntity domainToEntity(Bid bid, AuctionEntity auctionEntity) {
        return BidEntity.builder()
                .id(bid.getId())
                .bidderId(bid.getBidderId())
                .bidPrice(bid.getBidPrice())
                .bidTime(bid.getBidTime())
                .auctionEntity(auctionEntity)
                .build();
    }

    public static Bid dtoToDomain(BidRequestDto bidDto, Long userId) {
        return Bid.create(userId, bidDto.getAuctionId(), bidDto.getBidPrice());
    }


    public static BidResponseDto domainToDto(Bid bid) {
        return BidResponseDto.builder()
                .id(bid.getId())
                .bidderId(bid.getBidderId())
                .bidPrice(bid.getBidPrice())
                .auctionId(bid.getAuctionId())
                .build();
    }
}
