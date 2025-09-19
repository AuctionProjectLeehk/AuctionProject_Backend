package com.leehk.auction.domain.auction.infrastructure;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository {

    AuctionEntity save(AuctionEntity auctionEntity);

    Optional<AuctionEntity> findById(Long id);

    List<AuctionEntity> findByStatus(String status);

    void delete(AuctionEntity auctionEntity);
}
