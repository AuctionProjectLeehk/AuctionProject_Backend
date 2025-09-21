package com.leehk.auction.domain.bid.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidJpaRepository extends BidRepository, JpaRepository<BidEntity, Long> {
}