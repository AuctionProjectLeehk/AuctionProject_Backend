package com.leehk.auction.domain.auction.converter;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.dto.AuctionRequestDto;
import com.leehk.auction.domain.auction.dto.AuctionResponseDto;
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
    
    public static AuctionEntity DomainToEntity(Auction auction) {
        return AuctionEntity.builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .build();
    }

    public static Auction DtoToDomain(AuctionRequestDto auctionDto) {
        return Auction.builder()
                .title(auctionDto.getTitle())
                .description(auctionDto.getDescription())
                .startPrice(auctionDto.getStartPrice())
                .currentPrice(auctionDto.getCurrentPrice())
                .startTime(auctionDto.getStartTime())
                .endTime(auctionDto.getEndTime())
                .status(auctionDto.getStatus())
                .build();
    }
    
    public static AuctionResponseDto DomainToDto(Auction auction) {
        return AuctionResponseDto.builder()
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .build();
    }
}
