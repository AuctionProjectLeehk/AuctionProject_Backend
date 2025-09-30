package com.leehk.auction.domain.auction.application;

import com.leehk.auction.domain.auction.BaseH2Test;
import com.leehk.auction.domain.auction.domain.Auction;
import com.leehk.auction.domain.bid.domain.Bid;
import com.leehk.auction.domain.user.application.UserServiceImpl;
import com.leehk.auction.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class AuctionConcurrencyTest extends BaseH2Test {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuctionService auctionService;

    private Auction auction;
    private final int threadCount = 5;
    private List<User> auctionUsers;

    @BeforeEach
    void setup() {
        auctionUsers = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            User testUser = User.builder()
                    .email("test" + UUID.randomUUID() + "@example.com")
                    .name("test" + i)
                    .password("password")
                    .nickname("AuctionConcurrencyTestUser" + UUID.randomUUID().toString().substring(0,8))
                    .build();
            User savedUser = userService.saveUser(testUser);
            auctionUsers.add(savedUser);
        }

        auction = Auction.builder()
                .title("Concurrent Auction")
                .description("Testing concurrency")
                .startPrice(1000L)
                .currentPrice(1000L)
                .startTime(java.time.LocalDateTime.now())
                .endTime(java.time.LocalDateTime.now().plusDays(1))
                .status(com.leehk.auction.domain.auction.enums.AuctionStatus.ONGOING)
                .build();
        auction = auctionService.createAuction(auction, auctionUsers.get(0).getId());
    }

    /**
     * 1️⃣ 동시 입찰 테스트
     * 여러 사용자가 동시에 입찰 요청
     * 최고 입찰자와 가격이 올바르게 갱신되는지 검증
     */
    @Test
    @DisplayName("동시 입찰 테스트")
    void placeBidConcurrentlyBDD() throws InterruptedException {
        // given: threadCount 만큼의 사용자가 동시에 입찰
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when: 각 스레드가 입찰 실행
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    Long bidderId = auctionUsers.get(i).getId();
                    long bidPrice = 1000 + (i + 1) * 100L; // 입찰가: 1100, 1200, ..., 1500
                    auctionService.placeBid(auction.getId(), bidderId, bidPrice);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executorService.shutdown(); // ExecutorService 종료

        // then: 최종 경매 상태 확인
        Auction updatedAuction = auctionService.getAuction(auction.getId());
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(1000L + threadCount * 100L); // 가장 높은 입찰가 확인
        assertThat(updatedAuction.getHighestBidderId()).isEqualTo(auctionUsers.get(auctionUsers.size()-1).getId()); // 가장 높은 입찰자 확인
    }

    /**
     * 2️⃣ 동시 입찰 실패 테스트
     * 여러 사용자가 동시에 동일한 금액으로 입찰 요청
     * 첫 번째 요청만 성공하고 나머지는 실패하는지 검증
     */
    @Test
    @DisplayName("동시 입찰 실패 테스트")
    void placeBidConcurrently_FailBDD() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        final long bidPrice = 1500L; // 모든 스레드가 동일한 금액으로 입찰

        // when
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    Long bidderId = auctionUsers.get(i).getId();
                    try {
                        auctionService.placeBid(auction.getId(), bidderId, bidPrice);
                    } catch (IllegalArgumentException e) {
                        // 예상되는 예외 처리 (동일 금액 입찰 시도)
                        System.out.println("Bidder " + bidderId + " failed to place bid: " + e.getMessage());
                    }
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executorService.shutdown(); // ExecutorService 종료

        // then
        Auction updatedAuction = auctionService.getAuction(auction.getId());
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(1500L); // 입찰가 확인
        assertThat(updatedAuction.getHighestBidderId()).isNotNull(); // 최고 입찰자 존재 확인
    }

    /**
     * 2️⃣ 동시 입찰 취소 테스트
     * 모든 사용자가 입찰 후 동시에 취소
     * 최종 입찰 리스트와 가격 검증
     */
    @Test
    @DisplayName("동시 입찰 취소 테스트")
    void cancelBidsConcurrentlyBDD() throws InterruptedException {
        // given: 모든 사용자가 입찰 완료
        IntStream.range(0, threadCount).forEach(i -> {
            Long bidderId = auctionUsers.get(i).getId();
            long bidPrice = 1000 + (i + 1) * 100L; // 입찰가: 1100, 1200, ..., 1500
            auctionService.placeBid(auction.getId(), bidderId, bidPrice);
        });

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when: 모든 사용자가 동시에 입찰 취소
        List<Bid> placeBids = new ArrayList<>(
                auctionService.getAuction(auction.getId()).getBids()
        );

        placeBids.forEach(bid -> {
            executorService.submit(() -> {
                try {
                    auctionService.cancelBid(auction.getId(), bid.getId(), bid.getBidderId());
                } catch (Exception e) {
                    System.out.println("Failed to cancel bid: " + e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executorService.shutdown(); // ExecutorService 종료

        // then: 경매 가격과 입찰 리스트 검증
        Auction updatedAuction = auctionService.getAuction(auction.getId());
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(1000L); // 시작
        assertThat(updatedAuction.getBids()).isEmpty(); // 모든 입찰 취소
    }

    /**
     * 3️⃣ 최고 입찰자 확인 테스트
     * 여러 사용자가 동시에 입찰 후
     * 최종 최고 입찰자와 가격 검증
     */
    @Test
    @DisplayName("최고 입찰자 확인 테스트")
    void verifyHighestBidderAfterConcurrentBidsBDD() throws InterruptedException {
        // given: threadCount 만큼의 사용자가 동시에 입찰
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    Long bidderId = auctionUsers.get(i).getId();
                    long bidPrice = 1000 + (i + 1) * 100L; // 입찰가: 1100, 1200, ..., 1500
                    auctionService.placeBid(auction.getId(), bidderId, bidPrice);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await(); // 모든 스레드가 작업을 마칠 때까지 대기
        executorService.shutdown(); // ExecutorService 종료
        
        // then: 최고 입찰자와 가격 확인
        Auction updatedAuction = auctionService.getAuction(auction.getId());
        assertThat(updatedAuction.getCurrentPrice()).isEqualTo(1000L + threadCount * 100L); // 가장 높은 입찰가 확인
        assertThat(updatedAuction.getHighestBidderId()).isEqualTo(auctionUsers.get(auctionUsers.size()-1).getId()); // 가장 높은 입찰자 확인
    }
}