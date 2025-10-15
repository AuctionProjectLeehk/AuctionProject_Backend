package com.leehk.auction.domain.auction.infrastructure;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.bid.domain.AutoBid;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.bid.infrastructure.AutoBidEntity;
import com.leehk.auction.domain.bid.infrastructure.BidEntity;
import com.leehk.auction.domain.user.infrastructure.UserEntity;
import com.leehk.auction.global.CollectionSyncHelper;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity ownerEntity;

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

    @OneToMany(mappedBy = "auctionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<AutoBidEntity> autoBids = new ArrayList<>();

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

//        // ---- bids 동기화 (충돌 방지 방식) ----
//        // map existing by id
//        Map<UUID, BidEntity> existingBidsById = this.bids.stream()
//                .filter(bidEntity -> bidEntity.getId() != null)
//                .collect(Collectors.toMap(BidEntity::getId, Function.identity()));
//
//        List<BidEntity> updatedBids = new ArrayList<>();
//        for (Bid bid : auction.getBids()) {
//            UUID bidId = bid.getId();
//            if (bidId != null && existingBidsById.containsKey(bidId)) {
//                // 기존 영속 객체 업데이트
//                BidEntity exist = existingBidsById.remove(bidId);
//                exist.updateFromDomain(bid);
//                updatedBids.add(exist);
//            } else {
//                // 새로 추가되는 입찰(영속화 전 상태) — AuctionEntity(this)를 연관관계로 넣어 새 객체 생성
//                BidEntity newEntity = BidEntity.builder()
//                        .id(bid.getId()) // 도메인에서 id를 생성하면 들어옴, 아니면 null
//                        .bidderId(bid.getBidderId())
//                        .bidPrice(bid.getBidPrice())
//                        .bidTime(bid.getBidTime())
//                        .auctionEntity(this)
//                        .build();
//                updatedBids.add(newEntity);
//            }
//        }
//
//        // 기존에 남아있는 existingBidsById 값들은 domain에서 제거된 것들 -> 컬렉션에서 제거(그리고 orphanRemoval 이면 DB에서 삭제됨)
//        for (BidEntity removed : existingBidsById.values()) {
//            this.bids.remove(removed); // orphanRemoval = true면 삭제 처리
//        }
//
//        // 이제 컬렉션을 현재 상태로 맞춤: (clear + addAll) — 기존 영속 객체 재사용됨
//        this.bids.clear();
//        this.bids.addAll(updatedBids);
//
//        // autoBids 동기화
//        // map existing by id
//        Map<UUID, AutoBidEntity> existingAutoBidsById = this.autoBids.stream()
//                .filter(ab -> ab.getId() != null)
//                .collect(Collectors.toMap(AutoBidEntity::getId, Function.identity()));
//
//        List<AutoBidEntity> updatedAutoBids = new ArrayList<>();
//        for (AutoBid autoBid : auction.getAutoBids()) {
//            UUID autoBidId = autoBid.getId();
//            if (autoBidId != null && existingAutoBidsById.containsKey(autoBidId)) {
//                AutoBidEntity existAutoBid = existingAutoBidsById.remove(autoBidId);
//                existAutoBid.updateFromDomain(autoBid);
//                updatedAutoBids.add(existAutoBid);
//            } else {
//                AutoBidEntity autoBidEntity = AutoBidEntity.builder()
//                        .id(autoBid.getId())
//                        .autoBidderId(autoBid.getAutoBidderId())
//                        .maxAutoBidPrice(autoBid.getMaxAutoBidPrice())
//                        .currentAutoBidPrice(autoBid.getCurrentAutoBidPrice())
//                        .active(autoBid.isActive())
//                        .createdAt(autoBid.getCreatedAt())
//                        .updatedAt(autoBid.getUpdatedAt())
//                        .auctionEntity(this)
//                        .build();
//                updatedAutoBids.add(autoBidEntity);
//            }
//        }
//
//        for (AutoBidEntity removed : existingAutoBidsById.values()) {
//            this.autoBids.remove(removed);
//        }
//
//        this.autoBids.clear();
//        this.autoBids.addAll(updatedAutoBids);

        // ---- bids 동기화 ----
        CollectionSyncHelper.sync(
                this.bids,
                auction.getBids(),
                Bid::getId,
                BidEntity::getId,
                BidEntity::updateFromDomain,
                domain -> BidEntity.builder()
                        .id(domain.getId())
                        .bidderId(domain.getBidderId())
                        .bidPrice(domain.getBidPrice())
                        .bidTime(domain.getBidTime())
                        .auctionEntity(this)
                        .build()
        );

        // ---- autoBids 동기화 ----
        CollectionSyncHelper.sync(
                this.autoBids,
                auction.getAutoBids(),
                AutoBid::getId,
                AutoBidEntity::getId,
                AutoBidEntity::updateFromDomain,
                domain -> AutoBidEntity.builder()
                        .id(domain.getId())
                        .autoBidderId(domain.getAutoBidderId())
                        .maxAutoBidPrice(domain.getMaxAutoBidPrice())
                        .currentAutoBidPrice(domain.getCurrentAutoBidPrice())
                        .active(domain.isActive())
                        .createdAt(domain.getCreatedAt())
                        .updatedAt(domain.getUpdatedAt())
                        .auctionEntity(this)
                        .build()
        );
    }

    public void addAutoBid(AutoBidEntity autoBidEntity) {
        autoBids.add(autoBidEntity);
        autoBidEntity.updateAuctionEntity(this);
    }
}
