package com.leehk.auction.domain.auction.infrastructure;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuctionJpaRepository extends AuctionRepository, JpaRepository<AuctionEntity, Long> {

    /**
     * 식별자로 경매 엔티티를 조회하며, 동시성 제어를 위해 비관적 쓰기 락을 설정합니다.
     * 해당 메서드는 3초 동안 락 획득을 시도합니다.
     *
     * @param id 조회할 경매 엔티티의 식별자 (null이 아니어야 함)
     * @return 경매 엔티티가 존재하면 Optional에 담아 반환하고, 없으면 빈 Optional 반환
     */
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)  // 비관적 락 설정
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000")})  // 3초 동안 락 대기
    @Query("select a from AuctionEntity a where a.id = :id")
    Optional<AuctionEntity> findByIdForUpdate(Long id);

    /**
     * 주어진 엔티티를 저장하고 즉시 데이터베이스에 반영합니다.
     *
     * @param auctionEntity 저장하고 반영할 엔티티 (null이 아니어야 함)
     * @return 저장되고 반영된 엔티티
     */
    @Override
    <S extends AuctionEntity> S saveAndFlush(S auctionEntity);
}