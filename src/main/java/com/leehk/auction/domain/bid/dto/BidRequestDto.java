package com.leehk.auction.domain.bid.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BidRequestDto {

    private long bidPrice;
    private LocalDateTime bidTime;
    private Long auctionId;
}
