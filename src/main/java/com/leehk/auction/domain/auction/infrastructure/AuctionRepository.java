package com.leehk.auction.domain.auction.infrastructure;

import com.leehk.auction.domain.auction.enums.AuctionStatus;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository {

    AuctionEntity save(AuctionEntity auctionEntity);

    Optional<AuctionEntity> findById(Long id);

    List<AuctionEntity> findByStatus(AuctionStatus auctionStatus);

    void delete(AuctionEntity auctionEntity);
}
