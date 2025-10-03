package com.leehk.auction.domain.bid.domain;

import com.leehk.auction.global.response.CustomException;
import com.leehk.auction.global.response.ErrorCode;
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

    public static AutoBid create(Long userId, Long auctionId, long maxAutoBidPrice, long currentAutoBidPrice) {
        if (currentAutoBidPrice > maxAutoBidPrice) {
            throw new CustomException(ErrorCode.INVALID_AUTO_BID_CURRENT_PRICE);
        }

        LocalDateTime now = LocalDateTime.now();
        return new AutoBid(UUID.randomUUID(),
                userId,
                auctionId,
                maxAutoBidPrice,
                currentAutoBidPrice,
                true,
                now,
                now
        );
    }

    public static AutoBid restore(UUID id, Long autoBidderId, Long auctionId, long maxAutoBidPrice, long currentAutoBidPrice, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new AutoBid(id, autoBidderId, auctionId, maxAutoBidPrice, currentAutoBidPrice, active, createdAt, updatedAt);
    }
    
    public AutoBid updateCurrentAutoBidPrice(Long newCurrentAutoBidPrice) {
        if (newCurrentAutoBidPrice > this.maxAutoBidPrice) {
            throw new CustomException(ErrorCode.INVALID_AUTO_BID_CURRENT_PRICE);
        }

        return new AutoBid(this.id, this.autoBidderId, this.auctionId,
                this.maxAutoBidPrice, newCurrentAutoBidPrice,
                this.active, this.createdAt, LocalDateTime.now());
    }

    public AutoBid updateMaxAutoBidPrice(long newMaxPrice) {
        if (newMaxPrice < this.currentAutoBidPrice) {
            throw new CustomException(ErrorCode.INVALID_AUTO_BID_MAX_PRICE);
        }

        return new AutoBid(this.id, this.autoBidderId, this.auctionId,
                newMaxPrice, this.currentAutoBidPrice,
                this.active, this.createdAt, LocalDateTime.now());
    }

    public AutoBid deactivate() {
        return new AutoBid(this.id, this.autoBidderId, this.auctionId,
                this.maxAutoBidPrice, this.currentAutoBidPrice,
                false, this.createdAt, LocalDateTime.now());
    }
}
