package com.leehk.auction.domain.bid.converter;

import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.dto.BidRequestDto;
import com.leehk.auction.domain.bid.dto.BidResponseDto;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;

public class BidConverter {

    public static Bid entityToDomain(BidEntity bidEntity) {
        return Bid.builder()
                .id(bidEntity.getId())
                .bidderId(bidEntity.getBidderId())
                .bidPrice(bidEntity.getBidPrice())
                .bidTime(bidEntity.getBidTime())
                .auctionId(bidEntity.getAuctionEntity().getId())
                .build();
    }

    public static BidEntity domainToEntity(Bid bid, Auction auction) {
        return BidEntity.builder()
                .id(bid.getId())
                .bidderId(bid.getBidderId())
                .bidPrice(bid.getBidPrice())
                .bidTime(bid.getBidTime())
                .auctionEntity(AuctionEntity.builder().id(auction.getId()).build())
                .build();
    }

    public static Bid dtoToDomain(BidRequestDto bidDto) {
        return Bid.builder()
                .bidderId(bidDto.getBidderId())
                .bidPrice(bidDto.getBidPrice())
                .auctionId(bidDto.getAuctionId())
                .build();
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
