package com.leehk.auction.domain.bid.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AutoBidResponseDto {

    private final UUID id;
    private final String autoBidderNickname;
    private final Long auctionId;
    private final long maxAutoBidPrice;
    private final long currentAutoBidPrice;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
