package com.leehk.auction.domain.bid.infrastructure;

import com.leehk.auction.domain.auction.converter.AuctionConverter;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.auction.enums.AuctionStatus;
import com.leehk.auction.domain.auction.infrastructure.AuctionEntity;
import com.leehk.auction.domain.auction.infrastructure.AuctionRepository;
import com.leehk.auction.domain.user.converter.UserConverter;
import com.leehk.auction.domain.user.domain.User;
import com.leehk.auction.domain.user.infrastructure.UserEntity;
import com.leehk.auction.domain.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AutoBidRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AutoBidRepository autoBidRepository;

    private User testUser;
    private Auction testAuction;

    @BeforeEach
    void setup() {
        testUser = User.builder()
                .publicId(UUID.randomUUID())
                .email("<EMAIL>")
                .name("test")
                .password("<PASSWORD>")
                .nickname("test")
                .build();

        testAuction = Auction.builder()
                .title("test 경매")
                .description("test 설명")
                .startPrice(1000L)
                .currentPrice(10000L)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .status(AuctionStatus.ONGOING)
                .build();
    }

    @Test
    @DisplayName("자동 입찰 저장 및 조회 테스트")
    void saveAndFindAutoBid() {
        // given
        UserEntity userEntity = UserConverter.domainToEntity(testUser);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        testAuction.assignOwner(UserConverter.entityToDomain(savedUserEntity));
        AuctionEntity auctionEntity = AuctionConverter.domainToEntity(testAuction);
        AuctionEntity savedAuctionEntity = auctionRepository.save(auctionEntity);

        AutoBidEntity autoBidEntity = AutoBidEntity.builder()
                .id(UUID.randomUUID())
                .autoBidderId(savedUserEntity.getId())
                .auctionEntity(savedAuctionEntity)
                .maxAutoBidPrice(50000L)
                .currentAutoBidPrice(1000L)
                .active(true)
                .build();

        autoBidRepository.save(autoBidEntity);

        // when & then
        // Id로 조회
        AutoBidEntity foundEntity = autoBidRepository.findById(autoBidEntity.getId()).orElseThrow();
        assertThat(foundEntity.getAutoBidderId()).isEqualTo(savedUserEntity.getId());

        // 활성화된 자동 입찰 조회
        List<AutoBidEntity> activeBids = autoBidRepository.findByAuctionEntity_IdAndActiveTrue(savedAuctionEntity.getId());
        assertThat(activeBids.size()).isEqualTo(1);

        // 특정 유저의 자동 입찰 조회
        AutoBidEntity autoBidEntityByUser = autoBidRepository.findByAuctionEntity_IdAndAutoBidderId(
                savedAuctionEntity.getId(), savedUserEntity.getId()
        ).orElseThrow();
        assertThat(autoBidEntityByUser.getMaxAutoBidPrice()).isEqualTo(50000L);  // 최대값 확인
        assertThat(autoBidEntityByUser.getCurrentAutoBidPrice()).isEqualTo(1000L);  // 현재값 확인
    }

    @Test
    @DisplayName("자동입찰 삭제 테스트")
    void deleteAutoBid() {
        // given
        UserEntity userEntity = UserConverter.domainToEntity(testUser);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        testAuction.assignOwner(UserConverter.entityToDomain(savedUserEntity));
        AuctionEntity auctionEntity = AuctionConverter.domainToEntity(testAuction);
        AuctionEntity savedAuctionEntity = auctionRepository.save(auctionEntity);

        AutoBidEntity autoBidEntity = AutoBidEntity.builder()
                .id(UUID.randomUUID())
                .autoBidderId(savedUserEntity.getId())
                .auctionEntity(savedAuctionEntity)
                .maxAutoBidPrice(50000L)
                .currentAutoBidPrice(1000L)
                .active(true)
                .build();

        AutoBidEntity savedAutoBidEntity = autoBidRepository.save(autoBidEntity);

        // when: 삭제
        autoBidRepository.delete(savedAutoBidEntity);

        // then: 존재하는지 확인
        assertThat(autoBidRepository.findById(savedAutoBidEntity.getId())).isEmpty();
    }
}
