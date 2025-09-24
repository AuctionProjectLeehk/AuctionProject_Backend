package com.leehk.auction.domain.bid.infrastructure;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bid")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BidEntity {

    @Id
    private UUID id;

    private Long bidderId;

    private long bidPrice;

    private LocalDateTime bidTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    @JsonBackReference
    private AuctionEntity auctionEntity;

    public void updateAuctionEntity(AuctionEntity auctionEntity) {
        this.auctionEntity = auctionEntity;
    }
}
