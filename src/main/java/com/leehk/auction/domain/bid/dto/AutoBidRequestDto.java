package com.leehk.auction.domain.bid.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AutoBidRequestDto {

    private Long auctionId;
    private long maxAutoBidPrice;
    private LocalDateTime createdAt;
}
