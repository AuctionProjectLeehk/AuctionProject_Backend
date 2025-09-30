package com.leehk.auction.domain.bid.infrastructure;

import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.bid.domain.AutoBid;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auto_bid")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AutoBidEntity {

    @Id
    private UUID id;

    private Long autoBidderId;

    private long maxAutoBidPrice;

    private long currentAutoBidPrice;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private AuctionEntity auctionEntity;

    public void udpateAuctionEntity(AuctionEntity auctionEntity) {
        this.auctionEntity = auctionEntity;
    }

    public void updateFromDomain(AutoBid autoBid) {
        this.autoBidderId = autoBid.getAutoBidderId();
        this.maxAutoBidPrice = autoBid.getMaxAutoBidPrice();
        this.currentAutoBidPrice = autoBid.getCurrentAutoBidPrice();
        this.active = autoBid.isActive();
        this.updatedAt = LocalDateTime.now();
        // auctionEntity는 변경하지 않음 (연관관계는 외부에서 관리)
    }
}
