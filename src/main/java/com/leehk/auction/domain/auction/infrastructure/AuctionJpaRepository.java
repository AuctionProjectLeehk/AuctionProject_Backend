package com.leehk.auction.domain.auction.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionJpaRepository extends AuctionRepository, JpaRepository<AuctionEntity, Long> {
}
