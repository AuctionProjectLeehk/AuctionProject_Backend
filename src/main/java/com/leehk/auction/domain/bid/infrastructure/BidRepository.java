package com.leehk.auction.domain.bid.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRepository {

    Optional<BidEntity> findById(UUID bidderId);

    BidEntity save(BidEntity bidEntity);

    List<BidEntity> findByAuctionEntity_Id(Long auctionId);

    Optional<BidEntity> findTopByAuctionEntity_IdOrderByBidPriceDesc(Long auctionId);

    void delete(BidEntity bidEntity);
}
