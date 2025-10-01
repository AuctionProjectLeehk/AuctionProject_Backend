package com.leehk.auction.domain.bid.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutoBidRepository {

    Optional<AutoBidEntity> findById(UUID id);

    AutoBidEntity save(AutoBidEntity autoBidEntity);

    List<AutoBidEntity> findByAuctionEntity_Id(Long auctionId);

    List<AutoBidEntity> findActiveByAuctionEntity_Id(Long auctionId);

    List<AutoBidEntity> findByAuctionEntity_IdAndActiveTrue(Long auctionId);

    Optional<AutoBidEntity> findByAuctionEntity_IdAndAutoBidderId(Long auctionId, Long bidderId);

    void delete(AutoBidEntity autoBidEntity);
}
