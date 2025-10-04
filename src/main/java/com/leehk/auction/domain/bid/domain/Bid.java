package com.leehk.auction.domain.bid.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public final class Bid {

    private final UUID id;
    private final Long bidderId; // 입찰자 ID
    private final Long auctionId; // 경매 ID  
    private final long bidPrice; // 입찰가
    private final LocalDateTime bidTime; // 입찰 시간

    private Bid(UUID id, Long bidderId, Long auctionId, long bidPrice, LocalDateTime bidTime) {
        this.id = id;
        this.bidderId = bidderId;
        this.auctionId = auctionId;
        this.bidPrice = bidPrice;
        this.bidTime = bidTime;
    }

    /**
     * 주어진 파라미터로 새로운 입찰 객체를 생성합니다.
     *
     * @param bidderId  입찰을 하는 입찰자의 ID
     * @param auctionId 입찰이 이루어지는 경매의 ID
     * @param bidPrice  입찰가
     * @return 입찰 정보가 담긴 새로운 Bid 객체
     */
    public static Bid create(Long bidderId, Long auctionId, long bidPrice) {
        return new Bid(
                UUID.randomUUID(),
                bidderId,
                auctionId,
                bidPrice,
                LocalDateTime.now()
        );
    }

    /**
     * 기존 입찰 정보를 복원합니다.
     *
     * @param id        입찰의 고유 식별자
     * @param bidderId  입찰자의 ID
     * @param auctionId 경매 ID
     * @param bidPrice  입찰가
     * @param bidTime   입찰 시간
     * @return 복원된 입찰 정보를 담은 Bid 객체
     */
    public static Bid restore(UUID id, Long bidderId, Long auctionId, long bidPrice, LocalDateTime bidTime) {
        return new Bid(id, bidderId, auctionId, bidPrice, bidTime);
    }
}