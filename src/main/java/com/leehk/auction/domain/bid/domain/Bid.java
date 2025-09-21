package com.leehk.auction.domain.bid.domain;

import com.leehk.auction.domain.auction.domain.Auction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Bid {

    private Long id;
    private Long bidderId;
    private long bidPrice;

    @Builder.Default
    private LocalDateTime bidTime = LocalDateTime.now();
    private Auction auction;
}
