package com.leehk.auction.domain.auction.infrastructure;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.bid.converter.BidConverter;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Setter
    @Column(nullable = false)
    private long currentPrice;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @OneToMany(mappedBy = "auctionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<BidEntity> bids = new ArrayList<>();

    public void addBid(BidEntity bidEntity) {
        bids.add(bidEntity);
        bidEntity.updateAuctionEntity(this);
        this.currentPrice = bidEntity.getBidPrice();
    }

    public void updateFromDomain(Auction auction) {
        this.title = auction.getTitle();
        this.description = auction.getDescription();
        this.startPrice = auction.getStartPrice();
        this.currentPrice = auction.getCurrentPrice();
        this.startTime = auction.getStartTime();
        this.endTime = auction.getEndTime();
        this.status = auction.getStatus();
        this.bids = auction.getBids().stream()
                .map(bid -> BidConverter.domainToEntity(bid, auction)) // this = 현재 AuctionEntity
                .collect(Collectors.toList()); // mutable
    }
}
