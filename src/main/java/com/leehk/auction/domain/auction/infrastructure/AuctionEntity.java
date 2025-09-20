package com.leehk.auction.domain.auction.infrastructure;

import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private long startPrice;

    @Column(nullable = false)
    private long currentPrice;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    public void update(AuctionEntity auctionEntity) {
        this.title = auctionEntity.getTitle();
        this.description = auctionEntity.getDescription();
        this.startPrice = auctionEntity.getStartPrice();
        this.currentPrice = auctionEntity.getCurrentPrice();
        this.startTime = auctionEntity.getStartTime();
        this.endTime = auctionEntity.getEndTime();
        this.status = auctionEntity.getStatus();
    }

    public void updateFromDomain(Auction auction) {
        this.title = auction.getTitle();
        this.description = auction.getDescription();
        this.startPrice = auction.getStartPrice();
        this.currentPrice = auction.getCurrentPrice();
        this.startTime = auction.getStartTime();
        this.endTime = auction.getEndTime();
        this.status = auction.getStatus();
    }
}
