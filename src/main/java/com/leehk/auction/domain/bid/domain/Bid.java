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
    private LocalDateTime bidTime;
    private Auction auction;

    public Bid(Long bidderId, long bidPrice, Auction auction) {
        this.bidderId = bidderId;
        this.bidPrice = bidPrice;
        this.auction = auction;
        this.bidTime = LocalDateTime.now();
    }
}
