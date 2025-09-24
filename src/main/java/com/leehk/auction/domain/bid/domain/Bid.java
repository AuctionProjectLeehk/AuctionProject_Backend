package com.leehk.auction.domain.bid.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public final class Bid {

    private final UUID id;
    private final Long bidderId;
    private final Long auctionId;
    private final long bidPrice;
    private final LocalDateTime bidTime;

    private Bid(UUID id, Long bidderId, Long auctionId, long bidPrice, LocalDateTime bidTime) {
        this.id = id;
        this.bidderId = bidderId;
        this.auctionId = auctionId;
        this.bidPrice = bidPrice;
        this.bidTime = bidTime;
    }

    public static Bid create(Long bidderId, Long auctionId, long bidPrice) {
        return new Bid(
                UUID.randomUUID(),
                bidderId,
                auctionId,
                bidPrice,
                LocalDateTime.now()
        );
    }

    public static Bid restore(UUID id, Long bidderId, Long auctionId, long bidPrice, LocalDateTime bidTime) {
        return new Bid(id, bidderId, auctionId, bidPrice, bidTime);
    }
}
