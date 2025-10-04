package com.leehk.auction.domain.bid.converter;

import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.bid.domain.AutoBid;
import com.leehk.auction.domain.bid.dto.AutoBidRequestDto;
import com.leehk.auction.domain.bid.dto.AutoBidResponseDto;
import com.leehk.auction.domain.bid.infrastructure.AutoBidEntity;

public class AutoBidConverter {

    public static AutoBid entityToDomain(AutoBidEntity autoBidEntity) {
        return AutoBid.restore(
                autoBidEntity.getId(),
                autoBidEntity.getAutoBidderId(),
                autoBidEntity.getAuctionEntity().getId(),
                autoBidEntity.getMaxAutoBidPrice(),
                autoBidEntity.getCurrentAutoBidPrice(),
                autoBidEntity.isActive(),
                autoBidEntity.getCreatedAt(),
                autoBidEntity.getUpdatedAt()
        );
    }

    public static AutoBidEntity domainToEntity(AutoBid autoBid, AuctionEntity auctionEntity) {
        return AutoBidEntity.builder()
                .id(autoBid.getId())
                .autoBidderId(autoBid.getAutoBidderId())
                .maxAutoBidPrice(autoBid.getMaxAutoBidPrice())
                .currentAutoBidPrice(autoBid.getCurrentAutoBidPrice())
                .active(autoBid.isActive())
                .createdAt(autoBid.getCreatedAt())
                .updatedAt(autoBid.getUpdatedAt())
                .auctionEntity(auctionEntity)
                .build();
    }
    
    public static AutoBid dtoToDomain(AutoBidRequestDto autoBidDto, Long userId) {
        return AutoBid.create(userId, autoBidDto.getAuctionId(), autoBidDto.getMaxAutoBidPrice(), 0);
    }
    
    public static AutoBidResponseDto domainToDto(AutoBid autoBid) {
        return AutoBidResponseDto.builder()
                .id(autoBid.getId())
                .autoBidderNickname("User" + autoBid.getAutoBidderId()) // TODO: 수정해야 함
                .auctionId(autoBid.getAuctionId())
                .maxAutoBidPrice(autoBid.getMaxAutoBidPrice())
                .currentAutoBidPrice(autoBid.getCurrentAutoBidPrice())
                .active(autoBid.isActive())
                .createdAt(autoBid.getCreatedAt())
                .updatedAt(autoBid.getUpdatedAt())
                .build();
    }
}
