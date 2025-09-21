package com.leehk.auction.domain.bid.infrastructure;

import java.util.List;
import java.util.Optional;

public interface BidRepository {

    Optional<BidEntity> findById(Long bidId);

    BidEntity save(BidEntity bidEntity);

    List<BidEntity> findByAuctionEntity_Id(Long auctionId);

    void delete(BidEntity bidEntity);
}
