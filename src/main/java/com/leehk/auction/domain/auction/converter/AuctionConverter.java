package com.leehk.auction.domain.auction.converter;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.dto.AuctionRequestDto;
import com.leehk.auction.domain.auction.dto.AuctionResponseDto;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.bid.converter.BidConverter;
import com.leehk.auction.domain.bid.dto.BidResponseDto;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;
import com.leehk.auction.domain.user.converter.UserConverter;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.user.infrastructure.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class AuctionConverter {

    // Entity → Domain
    public static Auction entityToDomain(AuctionEntity auctionEntity) {
        User owner = UserConverter.entityToDomain(auctionEntity.getOwnerEntity());

        return Auction.builder()
                .id(auctionEntity.getId())
                .owner(owner)
                .title(auctionEntity.getTitle())
                .description(auctionEntity.getDescription())
                .startPrice(auctionEntity.getStartPrice())
                .currentPrice(auctionEntity.getCurrentPrice())
                .startTime(auctionEntity.getStartTime())
                .endTime(auctionEntity.getEndTime())
                .status(auctionEntity.getStatus())
                .bids(auctionEntity.getBids().stream()
                        .map(BidConverter::entityToDomain)
                        .collect(Collectors.toList())) // mutable
                .build();
    }

    // Domain → Entity (전체 변환)
    public static AuctionEntity domainToEntity(Auction auction) {
        UserEntity ownerEntity = UserConverter.domainToEntity(auction.getOwner());

        AuctionEntity auctionEntity = AuctionEntity.builder()
                .id(auction.getId())
                .ownerEntity(ownerEntity)
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .build();

        // bids 변환
        auction.getBids().forEach(bid -> {
            BidEntity bidEntity = BidConverter.domainToEntity(bid, auctionEntity);
            auctionEntity.addBid(bidEntity);
        });

        return auctionEntity;
    }

    // DTO → Domain
    public static Auction dtoToDomain(AuctionRequestDto auctionDto, User owner) {
        return Auction.builder()
                .id(auctionDto.getId())
                .owner(owner)
                .title(auctionDto.getTitle())
                .description(auctionDto.getDescription())
                .startPrice(auctionDto.getStartPrice())
                .currentPrice(auctionDto.getCurrentPrice())
                .startTime(auctionDto.getStartTime())
                .endTime(auctionDto.getEndTime())
                .status(auctionDto.getStatus())
                .build();
    }

    // Domain → DTO
    public static AuctionResponseDto domainToDto(Auction auction) {
        List<BidResponseDto> bidResponseDtos = auction.getBids().stream()
                .map(BidConverter::domainToDto)
                .collect(Collectors.toList());

        return AuctionResponseDto.builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .bids(bidResponseDtos)
                .build();
    }
}
