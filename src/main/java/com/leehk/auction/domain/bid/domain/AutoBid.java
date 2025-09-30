package com.leehk.auction.domain.bid.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AutoBid {

    private final UUID id;
    private final Long autoBidderId;
    private final Long auctionId;
    private final long maxAutoBidPrice;
    private final long currentAutoBidPrice;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private AutoBid(UUID id, Long autoBidderId, Long auctionId, long maxAutoBidPrice, long currentAutoBidPrice, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.autoBidderId = autoBidderId;
        this.auctionId = auctionId;
        this.maxAutoBidPrice = maxAutoBidPrice;
        this.currentAutoBidPrice = currentAutoBidPrice;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AutoBid create(Long userId, Long auctionId, long maxAutoBidPrice) {
        LocalDateTime now = LocalDateTime.now();
        return new AutoBid(UUID.randomUUID(),
                userId,
                auctionId,
                maxAutoBidPrice,
                0,
                true,
                now,
                now
        );
    }

    public static AutoBid restore(UUID id, Long autoBidderId, Long auctionId, long maxAutoBidPrice, long currentAutoBidPrice, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new AutoBid(id, autoBidderId, auctionId, maxAutoBidPrice, currentAutoBidPrice, active, createdAt, updatedAt);
    }
}
