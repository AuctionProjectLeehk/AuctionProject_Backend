package com.leehk.auction.domain.bid.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidResponseDto {

    private Long id;
    private Long bidderId;
    private long bidPrice;
    private Long auctionId;
}
