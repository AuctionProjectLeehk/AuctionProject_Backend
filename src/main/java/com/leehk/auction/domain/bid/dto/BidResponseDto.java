package com.leehk.auction.domain.bid.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BidResponseDto {

    private UUID id;
    private Long bidderId;
    private long bidPrice;
    private Long auctionId;
    private LocalDateTime bidTime;
}
